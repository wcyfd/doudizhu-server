package com.randioo.doudizhu_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Fight.FightReadyRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;

@Controller
@PTAnnotation(FightReadyRequest.class)
public class FightReadyAction implements IActionSupport {

	@Autowired
	private FightService fightService;

	@Override
	public void execute(Object data, IoSession session) {
		FightReadyRequest request = (FightReadyRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		fightService.readyGame(role);
	}

}
