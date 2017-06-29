package com.randioo.doudizhu_server.module.heartbeat.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.heartbeat.service.HeartbeatService;
import com.randioo.doudizhu_server.protocol.Heartbeat.HeartbeatRequest;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(HeartbeatRequest.class)
public class HeartbeatAction implements IActionSupport {

	@Autowired
	private HeartbeatService heartbeatService;
	
	@Override
	public void execute(Object data, IoSession session) {
		HeartbeatRequest request = (HeartbeatRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage sc = heartbeatService.setHeartbeat(role);
		SessionUtils.sc(role.getRoleId(), sc);
	}

}
