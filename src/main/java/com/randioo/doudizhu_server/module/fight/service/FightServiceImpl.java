package com.randioo.doudizhu_server.module.fight.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.comparator.HexCardComparator;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.entity.po.cardlist.A1;
import com.randioo.doudizhu_server.entity.po.cardlist.A2;
import com.randioo.doudizhu_server.entity.po.cardlist.A2B2C2;
import com.randioo.doudizhu_server.entity.po.cardlist.A3;
import com.randioo.doudizhu_server.entity.po.cardlist.A3B3;
import com.randioo.doudizhu_server.entity.po.cardlist.A3B3CD;
import com.randioo.doudizhu_server.entity.po.cardlist.A3N1;
import com.randioo.doudizhu_server.entity.po.cardlist.A3N2;
import com.randioo.doudizhu_server.entity.po.cardlist.A4;
import com.randioo.doudizhu_server.entity.po.cardlist.A4BC;
import com.randioo.doudizhu_server.entity.po.cardlist.ABCDE;
import com.randioo.doudizhu_server.entity.po.cardlist.CardList;
import com.randioo.doudizhu_server.entity.po.cardlist.KQ;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.FightConstant;
import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Fight.FightAgreeExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightReadyResponse;
import com.randioo.doudizhu_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameDismiss;
import com.randioo.doudizhu_server.protocol.Fight.SCFightReady;
import com.randioo.doudizhu_server.protocol.Fight.SCFightStart;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.CardTools;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private HexCardComparator hexCardComparator;

	@Override
	public void init() {
		Map<Class<? extends CardList>, CardList> cardLists = GameCache.getCardLists();
		cardLists.put(A1.class, new A1());
		cardLists.put(A2.class, new A2());
		cardLists.put(A3.class, new A3());
		cardLists.put(A4.class, new A4());
		cardLists.put(A3N1.class, new A3N1());
		cardLists.put(A3N2.class, new A3N2());
		cardLists.put(A3B3.class, new A3B3());
		cardLists.put(KQ.class, new KQ());
		cardLists.put(ABCDE.class, new ABCDE());
		cardLists.put(A2B2C2.class, new A2B2C2());
		cardLists.put(A3B3CD.class, new A3B3CD());
		cardLists.put(A4BC.class, new A4BC());

		GameCache.getSendCardSeqCheckerList().add(GameCache.getCardLists().get(A4.class));
		GameCache.getSendCardSeqCheckerList().add(GameCache.getCardLists().get(KQ.class));
	}

	@Override
	public void readyGame(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder()
							.setFightReadyResponse(
									FightReadyResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
			return;
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		// 游戏准备
		SessionUtils.sc(roleGameInfo.roleId,
				SC.newBuilder().setFightReadyResponse(FightReadyResponse.newBuilder()).build());

		roleGameInfo.ready = true;
		SC scFightReady = SC.newBuilder().setSCFightReady(SCFightReady.newBuilder().setGamrRoleId(gameRoleId)).build();
		for (RoleGameInfo info : game.getRoleIdMap().values())
			SessionUtils.sc(info.roleId, scFightReady);

		// 检查是否全部都准备完毕,全部准备完毕开始游戏
		boolean allReady = true;
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (!info.ready) {
				allReady = false;
				break;
			}
		}
		if (allReady) {
			game.setGameState(GameState.GAME_START_START);

			SC scFightStart = SC.newBuilder().setSCFightStart(SCFightStart.newBuilder()).build();
			for (RoleGameInfo info : game.getRoleIdMap().values())
				SessionUtils.sc(info.roleId, scFightStart);
		}
	}

	@Override
	public GeneratedMessage exitGame(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			return SC.newBuilder()
					.setFightExitGameResponse(
							FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build();
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

		GameState gameState = game.getGameState();
		// 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
		if (gameState == GameState.GAME_STATE_PREPARE) {
			// 若是房主，则直接解散
			if (game.getMasterRoleId() == role.getRoleId()) {
				game.setGameState(GameState.GAME_START_END);

				SC scDismiss = SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
				for (RoleGameInfo info : game.getRoleIdMap().values())
					SessionUtils.sc(info.roleId, scDismiss);

				// 将游戏从缓存池中移除
				GameCache.getGameMap().remove(game.getGameId());
			} else {
				// 该玩家退出
				SC scExit = SC.newBuilder().setSCFightExitGame(SCFightExitGame.newBuilder().setGameRoleId(gameRoleId))
						.build();
				for (RoleGameInfo info : game.getRoleIdMap().values())
					SessionUtils.sc(info.roleId, scExit);
				game.getRoleIdMap().remove(gameRoleId);
			}

		}
		// 如果游戏已经开始,则要申请退出
		else if (gameState == GameState.GAME_START_START) {
			if (game.getOnlineRoleCount() != 0) {
				// TODO 申请退出ing
				System.out.println(role.getRoleId() + "申请退出ing");
				return null;
			}
			SC scApplyExit = SC.newBuilder()
					.setSCFightApplyExitGame(SCFightApplyExitGame.newBuilder().setGameRoleId(gameRoleId)).build();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				if (SessionCache.getSessionById(info.roleId).isConnected()) {
					game.setOnlineRoleCount(game.getOnlineRoleCount() + 1);
				}
				info.agreeLeave = null;
				SessionUtils.sc(info.roleId, scApplyExit);
			}
			agreeExit(role, true);
		}

		return SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build();
	}

	@Override
	public GeneratedMessage agreeExit(Role role, boolean agree) {
		System.out.println(role.getRoleId() + "" + agree);
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			return null;
		} else {
			String roleInfoStr = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
			RoleGameInfo roleInfo = game.getRoleIdMap().get(roleInfoStr);
			roleInfo.agreeLeave = agree;
			game.getRoleIdMap().put(roleInfoStr, roleInfo);
			int flag = 0;
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				if (info.agreeLeave != null && info.agreeLeave == false) {
					game.setOnlineRoleCount(0);
					// TODO 申请结束
					return null;
				}
				if (info.agreeLeave != null && info.agreeLeave) {
					flag += 1;
				}
			}
			if (flag == game.getOnlineRoleCount()) {
				game.setGameState(GameState.GAME_START_END);

				SC scDismiss = SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
				for (RoleGameInfo info : game.getRoleIdMap().values())
					SessionUtils.sc(info.roleId, scDismiss);

				// 将游戏从缓存池中移除
				GameCache.getGameMap().remove(game.getGameId());
			}
		}

		return SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build();
	}

	private void dispatchCard(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		for (RoleGameInfo info : game.getRoleIdMap().values())
			info.cards.clear();

		game.getRoleIdList().addAll(game.getRoleIdMap().keySet());

		int maxCount = game.getMaxRoleCount();
		int needCard = 1;
		int totalCardCount = (FightConstant.CARDS.length - maxCount) / maxCount;
		for (int j = 0; j < needCard; j++) {
			// 先添加所有的牌,然后逐一随机拿走
			List<Integer> list = new ArrayList<>(FightConstant.CARDS.length);
			for (int i = 0; i < FightConstant.CARDS.length; i++)
				list.add(FightConstant.CARDS[i]);

			for (int i = 0; i < totalCardCount; i++) {
				for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
					int index = RandomUtils.getRandomNum(list.size());
					int value = list.get(index);
					list.remove(index);

					roleGameInfo.cards.add(value);
				}
			}

			// 对所有玩家的卡牌进行排序
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values())
				Collections.sort(roleGameInfo.cards, hexCardComparator);

			// 剩下的牌是地主牌
			game.getLandlordCards().addAll(list);
		}
	}

	public void send(Role role, List<Integer> paiList) {
		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);

		CardList lastCardList = game.getLastCardList();

		int index = game.getCurrentRoleIdIndex();
		String gameRoleId = game.getRoleIdList().get(index);

		// 检查是否应该是这个人出牌
		if (role.getRoleId() != game.getRoleIdMap().get(gameRoleId).roleId)
			return;

		CardList sendCardList = null;
		// 如果长度是0，说明不出牌，则要检查是否允许不出牌
		if (paiList.size() == 0) {
			if (game.getPassCount() >= (game.getMaxRoleCount() - 1)) {
				// 不允许不出牌
				return;
			} else {
				// 允许不出牌
				game.setPassCount(game.getPassCount() + 1);
			}
		} else {
			// 结构化卡牌，用于卡牌识别
			CardSort cardSort = new CardSort();
			CardTools.fillCardSort(cardSort, paiList);

			// 如果没有先前的牌型，则为主动出牌,直接判断牌型<br>否则通过使用上一轮牌型判断
			sendCardList = lastCardList == null ? initiativeSend(cardSort, paiList)
					: passiveSend(lastCardList.getClass(), cardSort, paiList);

			// 匹配牌型失败
			if (sendCardList == null)
				return;

			// 比较大小
			try {
				boolean bigger = sendCardListBiggerThanLastCardList(sendCardList, lastCardList);
				if (!bigger)
					return;
			} catch (CardTypeComparableException e) {
				return;
			}
		}
		// 设置最后一个人的牌型
		game.setLastCardList(sendCardList);
		// 将索引换到下一个人
		game.setCurrentRoleIdIndex((index + 1) >= game.getRoleIdList().size() ? 0 : index + 1);
		// 如果出牌了，则放弃出牌的计数器重置
		if (sendCardList != null)
			game.setPassCount(0);

		/**
		 * 通知出牌
		 */
		notifyObservers(FightConstant.SEND_CARD, gameId, gameRoleId, sendCardList);

		// 检查游戏是否结束
		this.checkGameOver(gameId);
	}

	/**
	 * 主动出牌
	 * 
	 * @param cardSort
	 * @param paiList
	 * @return
	 */
	private CardList initiativeSend(CardSort cardSort, List<Integer> paiList) {
		for (CardList cardList : GameCache.getCardLists().values()) {
			CardList sendCardList = checkCardList(cardList, cardSort, paiList);
			if (sendCardList != null)
				return sendCardList;
		}
		return null;
	}

	/**
	 * 被动出牌
	 * 
	 * @param clazz
	 * @param cardSort
	 * @param paiList
	 * @return
	 */
	private CardList passiveSend(Class<?> clazz, CardSort cardSort, List<Integer> paiList) {
		CardList cardList = GameCache.getCardLists().get(clazz);
		CardList sendCardList = checkCardList(cardList, cardSort, paiList);

		if (sendCardList == null) {
			// 没有匹配成功，再查炸弹
			for (CardList checkCardList : GameCache.getSendCardSeqCheckerList()) {
				if (sendCardList == null) {
					sendCardList = checkCardList(checkCardList, cardSort, paiList);
					if (sendCardList != null)
						break;
				}
			}
		}
		return sendCardList;
	}

	/**
	 * 发送的牌是否比上一次的牌大
	 * 
	 * @param sendCardList
	 * @param lastCardList
	 * @return
	 */
	private boolean sendCardListBiggerThanLastCardList(CardList sendCardList, CardList lastCardList) {
		int compare = sendCardList.compareTo(lastCardList);
		if (compare <= 0)
			return false;

		return true;
	}

	/**
	 * 检查游戏是否结束
	 * 
	 * @param gameId
	 */
	public void checkGameOver(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.cards.size() == 0) {
				// 该玩家是赢家

				break;
			}
		}
	}

	/**
	 * 根据原有牌型查牌型
	 * 
	 * @param targetCardList
	 * @param cardSort
	 * @param paiList
	 * @return
	 */
	public CardList checkCardList(CardList targetCardList, CardSort cardSort, List<Integer> paiList) {
		try {
			return targetCardList.pattern(cardSort, paiList);
		} catch (CardListPatternException e) {
		}
		return null;
	}

	public static void main(String[] args) {
		// test_dispatchCard();
		test_linkedMap();

		Map<Integer, Integer> map = new HashMap<>();
		map.put(1, 1);
		map.put(2, 2);
		map.put(3, 3);
		Set<Integer> set = map.keySet();
		Set<Integer> set2 = map.keySet();
		map.remove(1);
		System.out.println(set);
	}

	private static void test_linkedMap() {
		Map<Integer, Integer> map = new LinkedHashMap<>();
		map.put(1, 1);
		map.put(2, 2);
		map.put(4, 4);
		map.put(5, 5);
		map.put(3, 3);
		map.put(6, 6);
		map.put(8, 8);
		map.put(7, 7);
		map.put(9, 9);
		map.put(10, 10);
		map.put(11, 11);
		map.put(12, 12);
		System.out.println(map.keySet());
	}

	private static void test_dispatchCard() {
		FightServiceImpl impl = new FightServiceImpl();
		Game game = new Game();
		game.setGameId(1);
		game.setMaxRoleCount(3);
		game.setMasterRoleId(1);

		GameCache.getGameMap().put(game.getGameId(), game);

		for (int i = 0; i < 3; i++) {
			RoleGameInfo info = new RoleGameInfo();
			info.gameRoleId = i + "";
			info.roleId = i;
			game.getRoleIdMap().put(info.gameRoleId, info);
		}

		impl.dispatchCard(1);

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			System.out.print(info.gameRoleId + "=");
			for (int i : info.cards)
				System.out.print(CardTools.toNum(i) + ",");
			System.out.println();
		}
		for (int card : game.getLandlordCards())
			System.out.print(CardTools.toNum(card) + ",");

	}

}
