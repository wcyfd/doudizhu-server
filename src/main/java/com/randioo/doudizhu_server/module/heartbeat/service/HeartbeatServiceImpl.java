package com.randioo.doudizhu_server.module.heartbeat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.protocol.Heartbeat.HeartbeatResponse;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("heartbeatService")
public class HeartbeatServiceImpl extends ObserveBaseService implements HeartbeatService {

	@Autowired
	private EventScheduler eventScheduler;
	
	@Override
	public void heartInit(Role role) {
		role.setHeartbeatTime(TimeUtils.getNowTime());
		add(role.getRoleId());		
	}

	private void add(int roleId) {
		TimeEvent timeEvent = createTimeEvent(roleId);
		eventScheduler.addEvent(timeEvent);
	}

	private TimeEvent createTimeEvent(int roleId) {
		DefaultTimeEvent defaultTimeEvent = new DefaultTimeEvent() {

			@Override
			public void update(TimeEvent timeEvent) {
				DefaultTimeEvent evt = (DefaultTimeEvent) timeEvent;
				if (checkConnect(evt)) {
					add(evt.getRoleId());
				}
			}
		};
		defaultTimeEvent.setEndTime(TimeUtils.getNowTime() + 5);
		defaultTimeEvent.setRoleId(roleId);
		return defaultTimeEvent;
	}

	private boolean checkConnect(DefaultTimeEvent evt) {
		Role role = (Role) RoleCache.getRoleById(evt.getRoleId());
		if (role.getHeartbeatTime() < evt.getEndTime() - 5) {
			SessionCache.getSessionById(role.getRoleId()).close(true);
			return false;
		}
		return true;
	}
	
	@Override
	public GeneratedMessage setHeartbeat(Role role) {
		role.setHeartbeatTime(TimeUtils.getNowTime());
		return SC.newBuilder().setHeartbeatResponse(HeartbeatResponse.newBuilder()).build();
	}
}
