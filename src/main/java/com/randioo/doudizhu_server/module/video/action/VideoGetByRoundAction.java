package com.randioo.doudizhu_server.module.video.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.module.video.service.VideoService;
import com.randioo.doudizhu_server.protocol.Video.VideoGetByRoundRequest;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(VideoGetByRoundRequest.class)
public class VideoGetByRoundAction implements IActionSupport {

	@Autowired
	private VideoService videoService;

	@Override
	public void execute(Object data, IoSession session) {
		VideoGetByRoundRequest request = (VideoGetByRoundRequest) data;
		GeneratedMessage sc = videoService.VideoGetByRound(request.getId(), request.getRound());
		SessionUtils.sc(session, sc);
	}

}
