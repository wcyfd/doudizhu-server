package com.randioo.doudizhu_server;

import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.utils.SpringContext;

public class MatchAITest {
	public static void test(){
		MatchService impl = SpringContext.getBean("matchService");
		Role role = new Role();
		role.setRoleId(1);
		role.setName("1");
		role.setAccount("1");
		role.setHeadImgUrl("");
		RoleCache.putNewRole(role);
		impl.matchAI(role);
	}
		
}
