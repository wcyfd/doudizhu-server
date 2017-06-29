package com.randioo.doudizhu_server.module.match.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.protocol.Entity.GameConfig;
import com.randioo.doudizhu_server.protocol.Entity.GameRoleData;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface MatchService extends ObserveBaseServiceInterface {
	/**
	 * 创建游戏
	 * 
	 * @param role
	 * @return
	 * @author wcy 2017年5月25日
	 */
	public void createRoom(Role role, GameConfig gameConfig);

	/**
	 * 加入游戏
	 * 
	 * @param role
	 * @param lockString
	 * @return
	 * @author wcy 2017年5月25日
	 */
	public void joinGame(Role role, String lockString);

	/**
	 * 获得游戏玩家标识符
	 * 
	 * @param gameId
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月25日
	 */
	String getGameRoleId(int gameId, int roleId);

	GeneratedMessage match(Role role);

	void matchAI(Role role);

	Role getRoleFromRoleGameInfo(RoleGameInfo info);

	void matchSucess(int gameId);

	GameRoleData parseGameRoleData(RoleGameInfo info, int gameId);

	GeneratedMessage matchCancel(Role role);

}
