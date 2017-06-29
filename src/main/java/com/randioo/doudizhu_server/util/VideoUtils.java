package com.randioo.doudizhu_server.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.bo.VideoData;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.protocol.Entity.video;
import com.randioo.randioo_server_base.cache.RoleCache;

public class VideoUtils {
	public static video parseVideo(VideoData data){
		try {
			return video.parseFrom(data.getVideo());
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	public static video parseVideoWithoutRecord(VideoData data){
		try {			
			return video.parseFrom(data.getVideo()).toBuilder().clearVideoRecord().clearKeyPoint().build();
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	public static VideoData toVideo(RoleGameInfo info,video v){
		VideoData data = new VideoData();
		data.setRoleId(info.roleId);
		Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
		data.setGameId(role.getGameId());
		data.setVideo(v.toByteArray());
		return data;
	}
	
}
