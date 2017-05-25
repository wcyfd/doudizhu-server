package com.randioo.doudizhu_server.module.match.service;

import java.util.Set;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.randioo_server_base.utils.template.Function;

public interface MatchService extends ObserveBaseServiceInterface {
	/**
	 * 创建游戏
	 * 
	 * @param role
	 * @return
	 * @author wcy 2017年5月25日
	 */
	public GeneratedMessage createGame(Role role);

	/**
	 * 加入游戏
	 * 
	 * @param role
	 * @param lockString
	 * @return
	 * @author wcy 2017年5月25日
	 */
	public GeneratedMessage joinGame(Role role, String lockString);

	/**
	 * 获得游戏玩家标识符
	 * 
	 * @param gameId
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月25日
	 */
	String getGameRoleId(int gameId, int roleId);

}
