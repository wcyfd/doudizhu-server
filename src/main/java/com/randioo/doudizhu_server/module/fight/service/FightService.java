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

	/**
	 * 获得最好的牌
	 * 
	 * @param role
	 * @author wcy 2017年5月31日
	 */
	void getBestCardList(Role role);

	/**
	 * 分牌
	 * 
	 * @param gameId
	 * @author wcy 2017年5月31日
	 */
	void dispatchCard(int gameId);

	/**
	 * 叫地主
	 * 
	 * @param role
	 * @param landlord
	 * @author wcy 2017年5月31日
	 */
	void callLandlord(Role role, int fen);
}
