package com.randioo.doudizhu_server.module.video.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.module.video.service.VideoService;
import com.randioo.doudizhu_server.protocol.Video.VideoGetByIdRequest;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(VideoGetByIdRequest.class)
public class VideoGetByIdAction implements IActionSupport {

	@Autowired
	private VideoService videoService;

	@Override
	public void execute(Object data, IoSession session) {
		VideoGetByIdRequest request = (VideoGetByIdRequest) data;
		GeneratedMessage sc = videoService.VideoGetById(request.getId());
		SessionUtils.sc(session, sc);
	}

}
