package com.randioo.doudizhu_server.module.video.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.dao.VideoDao;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.bo.VideoData;
import com.randioo.doudizhu_server.protocol.Entity.video;
import com.randioo.doudizhu_server.protocol.Entity.videoWithId;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.protocol.Video.VideoGetByIdResponse;
import com.randioo.doudizhu_server.protocol.Video.VideoGetByRoundResponse;
import com.randioo.doudizhu_server.protocol.Video.VideoGetResponse;
import com.randioo.doudizhu_server.util.VideoUtils;
import com.randioo.randioo_server_base.service.ObserveBaseService;

@Service("videoService")
public class VideoServiceImpl extends ObserveBaseService implements VideoService {
	
	@Autowired
	private VideoDao videoDao;
	
	@Override
	public GeneratedMessage VideoGet(Role role) {
		List<videoWithId> videoList = new ArrayList<>();
		
		for(VideoData v : videoDao.get(role.getRoleId())){
			videoList.add(videoWithId.newBuilder().setVideoMsg(VideoUtils.parseVideoWithoutRecord(v)).setVideoId(v.getId()).setRoleId(v.getRoleId()).build());
		}		
		return SC.newBuilder().setVideoGetResponse(VideoGetResponse.newBuilder().addAllVideoMsg(videoList)).build();
	}
	
	@Override
	public GeneratedMessage VideoGetById(int id) {		
		VideoData v = videoDao.getById(id);
		if(v == null){
			return SC.newBuilder().setVideoGetByIdResponse(VideoGetByIdResponse.newBuilder().setErrorCode(ErrorCode.NULL_REJECT.getNumber())).build();
		}		
		return SC.newBuilder().setVideoGetByIdResponse(VideoGetByIdResponse.newBuilder().setVideoMsg(videoWithId.newBuilder().setVideoMsg(VideoUtils.parseVideoWithoutRecord(v)).setVideoId(v.getId()).setRoleId(v.getRoleId()))).build();
	}

	@Override
	public GeneratedMessage VideoGetByRound(int id, int round) {		
		VideoData v = videoDao.getById(id);
		video DBVideo = VideoUtils.parseVideo(v);
		List<ByteString> SCList = DBVideo.getVideoRecordList();
		List<Integer> keypointList = DBVideo.getKeyPointList();
		List<ByteString> resultList = new ArrayList<>();
		if(v == null || round < 1 || keypointList.size() < round * 2 || keypointList.get(2 * round -2) >= keypointList.get(2 * round -1)){
			return SC.newBuilder().setVideoGetByRoundResponse(VideoGetByRoundResponse.newBuilder().setErrorCode(ErrorCode.NO_VIDEO.getNumber())).build();//ERROR
		}
		for(int i = 0; i < keypointList.get(0); i ++){
			resultList.add(SCList.get(i));
		}
		for(int i = keypointList.get(2 * round -2); i < keypointList.get(2 * round -1); i ++){
			resultList.add(SCList.get(i));
		}
		return SC.newBuilder().setVideoGetByRoundResponse(VideoGetByRoundResponse.newBuilder().setVideoMsg(video.newBuilder().addAllVideoRecord(resultList))).build();
	}


	

}
