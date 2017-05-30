package com.randioo.doudizhu_server.module.fight.service;

import java.util.List;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;

public interface FightService extends ObserveBaseServiceInterface {
	public void readyGame(Role role);

	GeneratedMessage exitGame(Role role);

	GeneratedMessage agreeExit(Role role, boolean agree);

	/**
	 * 真实玩家出牌
	 * 
	 * @param role
	 * @param paiList
	 */
	void sendCard(Role role, List<Integer> paiList);
}
