package com.randioo.doudizhu_server.module.fight.service;

import java.util.List;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface FightService extends ObserveBaseServiceInterface {
	public void readyGame(Role role);

	/**
	 * 退出比赛
	 * 
	 * @param role
	 * @return
	 * @author wcy 2017年6月29日
	 */
	void exitGame(Role role);

	/**
	 * 申请退出比赛
	 * 
	 * @param role
	 * @return
	 * @author wcy 2017年6月29日
	 */
	void applyExitGame(Role role);

	GeneratedMessage agreeExit(Role role, FightVoteApplyExit vote, int voteId);

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

	GeneratedMessage recommandCardList(Role role);

	void mingPai(Role role);

	GeneratedMessage getLastRecord(int gameId);

	void gameStart(int gameId);

	void rejoin(Role role);

	void callLandlord(int gameId, String gameRoleId, int callScore);

	void disconnectTimeUp(int roleId);

	GeneratedMessage auto(Role role);

}
