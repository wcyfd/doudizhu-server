package com.randioo.doudizhu_server.module.heartbeat.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface HeartbeatService extends ObserveBaseServiceInterface {

	void heartInit(Role role);

	GeneratedMessage setHeartbeat(Role role);
	
}
