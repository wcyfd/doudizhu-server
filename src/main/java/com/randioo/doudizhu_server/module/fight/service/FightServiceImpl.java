package com.randioo.doudizhu_server.module.fight.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.comparator.HexCardComparator;
import com.randioo.doudizhu_server.dao.VideoDao;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.AgreeExitTimeEvent;
import com.randioo.doudizhu_server.entity.po.CallLandLordTimeEvent;
import com.randioo.doudizhu_server.entity.po.CardRecord;
import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.entity.po.DisconnectTimeEvent;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.entity.po.SendCardTimeEvent;
import com.randioo.doudizhu_server.entity.po.cardlist.A1;
import com.randioo.doudizhu_server.entity.po.cardlist.A2;
import com.randioo.doudizhu_server.entity.po.cardlist.A2B2C2;
import com.randioo.doudizhu_server.entity.po.cardlist.A3;
import com.randioo.doudizhu_server.entity.po.cardlist.A3B3;
import com.randioo.doudizhu_server.entity.po.cardlist.A3B3C2D2;
import com.randioo.doudizhu_server.entity.po.cardlist.A3B3CD;
import com.randioo.doudizhu_server.entity.po.cardlist.A3N1;
import com.randioo.doudizhu_server.entity.po.cardlist.A3N2;
import com.randioo.doudizhu_server.entity.po.cardlist.A4;
import com.randioo.doudizhu_server.entity.po.cardlist.A4B2C2;
import com.randioo.doudizhu_server.entity.po.cardlist.A4BC;
import com.randioo.doudizhu_server.entity.po.cardlist.ABCDE;
import com.randioo.doudizhu_server.entity.po.cardlist.CardList;
import com.randioo.doudizhu_server.entity.po.cardlist.KQ;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.module.fight.FightConstant;
import com.randioo.doudizhu_server.module.match.service.MatchService;
import com.randioo.doudizhu_server.module.money.service.MoneyExchangeService;
import com.randioo.doudizhu_server.module.score.service.ScoreService;
import com.randioo.doudizhu_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.doudizhu_server.protocol.Entity.GameConfig;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.doudizhu_server.protocol.Entity.PaiNum;
import com.randioo.doudizhu_server.protocol.Entity.Record;
import com.randioo.doudizhu_server.protocol.Entity.video;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Fight.FightAgreeExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightApplyExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightAutoResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightCallLandLordResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightGetlastRoundResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightMingPaiResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightReadyResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightRecommandResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightRecommandResponse.RecommandPai;
import com.randioo.doudizhu_server.protocol.Fight.FightRejoinResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightSendCardResponse;
import com.randioo.doudizhu_server.protocol.Fight.SCFightAllAgreeExit;
import com.randioo.doudizhu_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightAuto;
import com.randioo.doudizhu_server.protocol.Fight.SCFightCallLandLord;
import com.randioo.doudizhu_server.protocol.Fight.SCFightExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightFinishRejoin;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameDismiss;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameOver;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameOver.GameOverData;
import com.randioo.doudizhu_server.protocol.Fight.SCFightLandLord;
import com.randioo.doudizhu_server.protocol.Fight.SCFightLastRoundReady;
import com.randioo.doudizhu_server.protocol.Fight.SCFightMingPai;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPutOut;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPutOutPai;
import com.randioo.doudizhu_server.protocol.Fight.SCFightReady;
import com.randioo.doudizhu_server.protocol.Fight.SCFightRejoin;
import com.randioo.doudizhu_server.protocol.Fight.SCFightRoundOver;
import com.randioo.doudizhu_server.protocol.Fight.SCFightRoundOver.Score;
import com.randioo.doudizhu_server.protocol.Fight.SCFightStart;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.CardTools;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.doudizhu_server.util.VideoUtils;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.ReflectUtils;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

	@Autowired
	private VideoDao videoDao;

	@Autowired
	private MatchService matchService;

	@Autowired
	private ScoreService scoreService;

	@Autowired
	private MoneyExchangeService moneyExchangeService;

	@Autowired
	private HexCardComparator hexCardComparator;

	@Autowired
	private EventScheduler eventScheduler;

	@Override
	public void init() {
		Map<Class<? extends CardList>, CardList> cardLists = GameCache.getCardLists();
		List<Class<? extends CardList>> classes = new ArrayList<>();

		classes.add(A1.class);
		classes.add(A2.class);
		classes.add(A3.class);
		classes.add(A4.class);
		classes.add(A3N1.class);
		classes.add(A3N2.class);
		classes.add(A3B3.class);
		classes.add(KQ.class);
		classes.add(ABCDE.class);
		classes.add(A2B2C2.class);
		classes.add(A3B3CD.class);
		classes.add(A3B3C2D2.class);
		classes.add(A4BC.class);
		classes.add(A4B2C2.class);

		for (Class<? extends CardList> clazz : classes)
			cardLists.put(clazz, ReflectUtils.newInstance(clazz));
		List<Class<? extends CardList>> recommendClasses = new ArrayList<>();
		Map<Class<? extends CardList>, CardList> recommendCardLists = GameCache.getRecommendCardLists();
		recommendClasses.add(A1.class);
		recommendClasses.add(A2.class);
		recommendClasses.add(A3.class);
		recommendClasses.add(A3N1.class);
		recommendClasses.add(A3N2.class);
		recommendClasses.add(ABCDE.class);
		recommendClasses.add(A2B2C2.class);
		recommendClasses.add(A3B3.class);
		recommendClasses.add(A3B3CD.class);
		recommendClasses.add(A3B3C2D2.class);
		// recommendClasses.add(A4BC.class);
		// recommendClasses.add(A4B2C2.class);
		recommendClasses.add(A4.class);
		recommendClasses.add(KQ.class);
		for (Class<? extends CardList> clazz : recommendClasses) {
			CardList cardList = cardLists.get(clazz);
			recommendCardLists.put(clazz, cardList);
		}

		GameCache.getSendCardSeqCheckerList().add(GameCache.getCardLists().get(A4.class));
		GameCache.getSendCardSeqCheckerList().add(GameCache.getCardLists().get(KQ.class));
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (msg.equals(FightConstant.FIGHT_RECORD)) {
			RoleGameInfo info = (RoleGameInfo) args[0];
			info.scList.add((SC) args[1]);
		}
		if (msg.equals(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD)) {
			Game game = GameCache.getGameMap().get(args[0]);
			String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
			RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
			System.out.println("@@@" + (game.getRoleIdList().indexOf(info.gameRoleId) == game.getCurrentRoleIdIndex()));
			if ((info.roleId == 0 && game.getRoleIdList().indexOf(info.gameRoleId) == game.getCurrentRoleIdIndex())) {
				CallLandLordTimeEvent callLandLordTimeEvent = new CallLandLordTimeEvent() {

					@Override
					public void update(TimeEvent timeEvent) {
						timeUp((CallLandLordTimeEvent) timeEvent);
					}
				};

				callLandLordTimeEvent.setGameRoleId(gameRoleId);
				callLandLordTimeEvent.setEndTime(TimeUtils.getNowTime() + 3/*
																			 * FightConstant
																			 * .
																			 * SEND_CARD_WAIT_TIME
																			 */);
				callLandLordTimeEvent.setGameId(game.getGameId());
				callLandLordTimeEvent.setScore(RandomUtils.getRandomNum(4));

				eventScheduler.addEvent(callLandLordTimeEvent);

				logger.info(info.roleId + "不叫");
			}
			// IoSession session = SessionCache.getSessionById(info.roleId);
			if (game.getGameType() == GameType.GAME_TYPE_MATCH) {
				CallLandLordTimeEvent callLandLordTimeEvent = new CallLandLordTimeEvent() {

					@Override
					public void update(TimeEvent timeEvent) {
						timeUp((CallLandLordTimeEvent) timeEvent);
					}
				};
				callLandLordTimeEvent.setGameRoleId(gameRoleId);
				callLandLordTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.SEND_CARD_WAIT_TIME / 2);
				callLandLordTimeEvent.setGameId(game.getGameId());
				callLandLordTimeEvent.setScore(0);

				eventScheduler.addEvent(callLandLordTimeEvent);
			}
		}
		if (msg.equals(FightConstant.APPLY_LEAVE)) {
			int gameId = (int) args[0];
			RoleGameInfo info = (RoleGameInfo) args[1];
			if (info.roleId == 0) {
				aiAgreeExit(gameId, info.gameRoleId);
			} else {
				AgreeExitTimeEvent agreeExitTimeEvent = new AgreeExitTimeEvent() {

					@Override
					public void update(TimeEvent timeEvent) {
						int gameId = ((AgreeExitTimeEvent) timeEvent).getGameId();
						String gameRoleId = ((AgreeExitTimeEvent) timeEvent).getGameRoleId();
						// TODO
						aiAgreeExit(gameId, gameRoleId);
					}
				};
				agreeExitTimeEvent.setGameId((int) args[0]);
				agreeExitTimeEvent.setGameRoleId(info.gameRoleId);
				agreeExitTimeEvent.setEndTime(TimeUtils.getNowTime() + 60);
			}
		}
		if (msg.equals(FightConstant.NEXT_GAME_ROLE_SEND_CARD)) {
			int gameId = (int) args[0];
			this.checkAutoAI(gameId);
		}
		if (msg.equals(FightConstant.FIGHT_MINGPAI)) {
			int roleId = (int) args[1];
			Game game = GameCache.getGameMap().get(args[0]);
			if (roleId == 0) {
				game.setMultiple(game.getMultiple() + 1);
				game.setMingPaiState(true);
				RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
				SC sc = SC
						.newBuilder()
						.setSCFightMingPai(
								SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
										.setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId))).build();
				for (RoleGameInfo info : game.getRoleIdMap().values()) {
					SessionUtils.sc(info.roleId, sc);
					this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());
				}
			}

		}
	}

	@Override
	public void initService() {
		this.addObserver(this);
	}

	/**
	 * 通知该场游戏的所有人
	 * 
	 * @param game
	 * @param sc
	 * @author wcy 2017年6月29日
	 */
	private void sendSC(Game game, SC sc) {
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values())
			SessionUtils.sc(roleGameInfo.roleId, sc);

	}

	@Override
	public GeneratedMessage auto(Role role) {// 托管
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			return SC
					.newBuilder()
					.setFightAutoResponse(
							FightAutoResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())).build();
		}
		if (game.getCallLandlordCount() < game.getMaxRoleCount() && game.getCallLandlordScore() < 3) {
			return SC
					.newBuilder()
					.setFightAutoResponse(
							FightAutoResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber())).build();
		}
		RoleGameInfo myInfo = null;
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.roleId == role.getRoleId()) {
				myInfo = info;
				break;
			}
		}
		if (myInfo == null) {
			return SC
					.newBuilder()
					.setFightAutoResponse(
							FightAutoResponse.newBuilder().setErrorCode(ErrorCode.NO_ROLE_DATA.getNumber())).build();
		}
		if (game.getGameState() != GameState.GAME_START_START) {
			return SC.newBuilder().setFightAutoResponse(FightAutoResponse.newBuilder()).build();
		}
		if (myInfo.auto >= 2) {// 托管解除
			myInfo.auto = 0;
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				SessionUtils.sc(
						info.roleId,
						SC.newBuilder()
								.setSCFightAuto(
										SCFightAuto.newBuilder()
												.setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId))
												.setAuto(false)).build());
				this.notifyObservers(
						FightConstant.FIGHT_RECORD,
						info,
						SC.newBuilder()
								.setSCFightAuto(
										SCFightAuto.newBuilder()
												.setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId))
												.setAuto(false)).build());
			}
		} else {
			myInfo.auto = 2;
			if (game.getRoleIdList().get(game.getCurrentRoleIdIndex()).equals(myInfo.gameRoleId)) {
				autoSendCard(game.getGameId(), myInfo.gameRoleId);// 自动出牌
			} else {
				for (RoleGameInfo info : game.getRoleIdMap().values()) {
					SessionUtils.sc(
							info.roleId,
							SC.newBuilder()
									.setSCFightAuto(
											SCFightAuto.newBuilder()
													.setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId))
													.setAuto(true)).build());
					this.notifyObservers(
							FightConstant.FIGHT_RECORD,
							info,
							SC.newBuilder()
									.setSCFightAuto(
											SCFightAuto.newBuilder()
													.setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId))
													.setAuto(false)).build());
				}
			}
		}
		return SC.newBuilder().setFightAutoResponse(FightAutoResponse.newBuilder()).build();
	}

	@Override
	public void readyGame(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightReadyResponse(
									FightReadyResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
			return;
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		// 游戏准备
		SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightReadyResponse(FightReadyResponse.newBuilder())
				.build());

		roleGameInfo.ready = true;
		SC scFightReady = SC
				.newBuilder()
				.setSCFightReady(
						SCFightReady.newBuilder().setSeated(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId))
								.setIsFirst(game.getGameConfig().getRound() == game.getRound())).build();
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			SessionUtils.sc(info.roleId, scFightReady);
			this.notifyObservers(FightConstant.FIGHT_RECORD, info, scFightReady);
		}

		// 检查是否全部都准备完毕,全部准备完毕开始游戏
		if (this.checkAllReady(role.getGameId())) {
			gameStart(game.getGameId());
		}
	}

	@Override
	public void rejoin(Role role) {// 重连
		int gameId = role.getGameId();
		/* System.out.println("@@@@"+gameId); */
		Game game = GameCache.getGameMap().get(gameId);
		if (game == null) {
			SessionUtils
					.sc(role.getRoleId(),
							SC.newBuilder()
									.setFightRejoinResponse(
											FightRejoinResponse.newBuilder().setErrorCode(
													ErrorCode.GAME_NOT_EXIST.getNumber())).build());
			return;
		}
		RoleGameInfo myInfo = null;
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.roleId == role.getRoleId()) {
				myInfo = info;
				break;
			}
		}
		if (myInfo == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightRejoinResponse(
									FightRejoinResponse.newBuilder().setErrorCode(ErrorCode.NO_ROLE_DATA.getNumber()))
							.build());
			return;
		}
		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setFightRejoinResponse(FightRejoinResponse.newBuilder().setErrorCode(-1)).build());
		myInfo.online = true;
		for (RoleGameInfo info : game.getRoleIdMap().values()) {// 通知其他玩家我已经重上好了
			if (info.roleId == role.getRoleId()) {
				this.notifyObservers(
						FightConstant.FIGHT_RECORD,
						info,
						SC.newBuilder()
								.setSCFightRejoin(
										SCFightRejoin.newBuilder().setSeated(
												game.getRoleIdList().indexOf(myInfo.gameRoleId))).build());
				continue;
			}
			SessionUtils.sc(
					info.roleId,
					SC.newBuilder()
							.setSCFightRejoin(
									SCFightRejoin.newBuilder().setSeated(
											game.getRoleIdList().indexOf(myInfo.gameRoleId))).build());
			this.notifyObservers(
					FightConstant.FIGHT_RECORD,
					info,
					SC.newBuilder()
							.setSCFightRejoin(
									SCFightRejoin.newBuilder().setSeated(
											game.getRoleIdList().indexOf(myInfo.gameRoleId))).build());
		}
		for (SC sc : myInfo.scList) {// 重连后逐条发通知
			SessionUtils.sc(myInfo.roleId, sc);
		}
		SessionUtils
				.sc(myInfo.roleId, SC.newBuilder().setSCFightFinishRejoin(SCFightFinishRejoin.newBuilder()).build());
		if (myInfo.auto >= 2) {// 重连后解除托管状态
			this.auto(role);
		}
		/*
		 * FightRejoinResponse.Builder rejoinBuilder =
		 * FightRejoinResponse.newBuilder(); List<gameRolePlayData>
		 * gameRoleDataList = new ArrayList<>(game.getRoleIdMap().size()); for
		 * (RoleGameInfo info : game.getRoleIdMap().values()) { if(info.roleId
		 * == 0){ continue; } gameRolePlayData.Builder gameRoleDataBuilder =
		 * gameRolePlayData.newBuilder();
		 * gameRoleDataBuilder.setGameRoleData(matchService
		 * .parseGameRoleData(info, game.getGameId()));
		 * gameRoleDataBuilder.setPaiNum(info.cards.size());
		 * gameRoleDataBuilder.setAllScore(info.allMark);
		 * gameRoleDataBuilder.setCallLandlordScore(info.callLandlordScore);
		 * gameRoleDataBuilder.setIsFarmer(true);
		 * gameRoleDataBuilder.setIsOnline(info.online);
		 * gameRoleDataBuilder.setIsAuto(game.getGameType() ==
		 * GameType.GAME_TYPE_MATCH && info.auto >= 2);
		 * if((game.getCallLandlordCount() >= game.getMaxRoleCount() ||
		 * game.getCallLandlordScore() >= 3) &&
		 * info.gameRoleId.equals(game.getLandlordGameRoleId())){
		 * gameRoleDataBuilder.setIsFarmer(false); }
		 * gameRoleDataList.add(gameRoleDataBuilder.build()); }
		 * rejoinBuilder.addAllRolePlayData(gameRoleDataList);
		 * rejoinBuilder.setRoomId(game.getLockString());
		 * rejoinBuilder.setGameId(String.valueOf(game.getGameId()));
		 * rejoinBuilder
		 * .setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId));
		 * rejoinBuilder.setGameState(GameState.GAME_STATE_PREPARE.getNumber());
		 * rejoinBuilder.setGameType(game.getGameType().getNumber());
		 * rejoinBuilder.setMingpai(game.getGameConfig().getMingpai());
		 * if(game.getGameState() == GameState.GAME_START_START){
		 * if(game.getCallLandlordCount() < game.getMaxRoleCount() &&
		 * game.getCallLandlordScore() < 3 ){
		 * rejoinBuilder.setLandlordCalling(true); }else{
		 * rejoinBuilder.addAllLandLordPai(game.getLandlordCards()); int loc =
		 * game.getRecords().get(game.getRecords().size()-1).size()-3; loc = loc
		 * < 0 ? 0 : loc; while(loc <
		 * game.getRecords().get(game.getRecords().size()-1).size()){ CardRecord
		 * temp = game.getRecords().get(game.getRecords().size()-1).get(loc);
		 * rejoinBuilder
		 * .addRecords(Record.newBuilder().setSeated(game.getRoleIdList
		 * ().indexOf(temp.gameRoleId)).addAllPai(temp.cards)); loc ++; } }
		 * rejoinBuilder.setMoguai(game.isMoGuai());
		 * rejoinBuilder.setGetLastRound
		 * (game.getRecords().get(game.getRecords().size()-1).size() >= 3);
		 * rejoinBuilder.addAllPai(myInfo.cards);
		 * rejoinBuilder.setTimes(game.getMultiple());
		 * rejoinBuilder.setGameState(GameState.GAME_START_START.getNumber()); }
		 * SessionUtils.sc(role.getRoleId(),
		 * SC.newBuilder().setFightRejoinResponse(rejoinBuilder).build()); SC sc
		 * = game.getCurrentStatusSC(); if(sc != null)
		 * SessionUtils.sc(role.getRoleId(), sc); if(game.getGameState() ==
		 * GameState.GAME_START_START && game.isMingPaiState()){ RoleGameInfo
		 * LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
		 * SessionUtils.sc(role.getRoleId(), SC .newBuilder()
		 * .setSCFightMingPai(
		 * SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
		 * .setSeated(game.
		 * getRoleIdList().indexOf(LandLord.gameRoleId))).build()); }
		 */

	}

	@Override
	public void gameStart(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		game.getStartTime().add(TimeUtils.getDetailTimeStr());
		game.getRecords().add(new ArrayList<CardRecord>());
		game.setGameState(GameState.GAME_START_START);
		// 游戏初始化
		this.gameInit(game.getGameId());

		game.setRound(game.getRound() - 1);
		SCFightStart.Builder FSBuilder = SCFightStart.newBuilder();
		for (int i = 0; i < game.getMaxRoleCount(); i++) {
			FSBuilder.addPaiNum(PaiNum.newBuilder().setSeated(i)
					.setNum(game.getRoleIdMap().get(game.getRoleIdList().get(i)).cards.size()));

		}
		FSBuilder.setTimes((int) Math.pow(2, game.getMultiple()));
		FSBuilder.setMoguai(game.isMoGuai());
		FSBuilder.setRoundNum(game.getRound());
		SC sc = SC
				.newBuilder()
				.setSCFightCallLandLord(
						SCFightCallLandLord.newBuilder().setCurrentFen(0).setSeated(game.getCurrentRoleIdIndex())
								.setCountdown(FightConstant.COUNTDOWN / 2).setFen(-1)).build();
		game.setCurrentStatusSC(sc);
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			info.auto = 0;
			info.videoRoundPoint.add(info.scList.size());
			System.out.println("~~~" + info.roleId + game.getMultiple());
			SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightStart(FSBuilder.clone().addAllPai(info.cards))
					.build());
			this.notifyObservers(FightConstant.FIGHT_RECORD, info,
					SC.newBuilder().setSCFightStart(FSBuilder.clone().addAllPai(info.cards)).build());
			SessionUtils.sc(info.roleId, sc);
			this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());

		}
		this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
	}

	/**
	 * 检查全部准备完毕
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年5月31日
	 */
	private boolean checkAllReady(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		if (game.getRoleIdMap().size() < game.getMaxRoleCount())
			return false;

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (!info.ready)
				return false;
		}
		return true;
	}

	/**
	 * 游戏初始化
	 * 
	 * @param gameId
	 * @author wcy 2017年5月31日
	 */
	private void gameInit(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		game.setMultiple(0);
		game.getLandlordCards().clear();
		game.setCallLandlordCount(0);
		game.setCallLandlordScore(0);
		game.setMingPaiState(false);
		game.setFarmerSpring(true);
		game.setLandLordSpring(true);
		game.setBomb(0);
		game.setMoGuai(false);
		game.setLastCardList(null);
		// 卡牌初始化
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			info.cards.clear();
		}

		dispatchCard(game.getGameId());

	}

	@Override
	public void mingPai(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightMingPaiResponse(
									FightMingPaiResponse.newBuilder()
											.setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())).build());
		}
		RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
		if (game.getRoleIdMap().get(game.getLandlordGameRoleId()).roleId != role.getRoleId()) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightMingPaiResponse(
									FightMingPaiResponse.newBuilder().setErrorCode(ErrorCode.NOT_LANDLORD.getNumber()))
							.build());
		}
		if (!game.getGameConfig().getMingpai()) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightMingPaiResponse(
									FightMingPaiResponse.newBuilder().setErrorCode(
											ErrorCode.MINGPAI_FORBIDDEN.getNumber())).build());
		}
		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightMingPaiResponse(FightMingPaiResponse.newBuilder())
				.build());
		game.setMultiple(game.getMultiple() + 1);
		game.setMingPaiState(true);
		SC sc = SC
				.newBuilder()
				.setSCFightMingPai(
						SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
								.setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId))).build();
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			SessionUtils.sc(info.roleId, sc);
			this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());
		}

	}

	@Override
	public GeneratedMessage getLastRecord(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		if (game == null) {
			return SC
					.newBuilder()
					.setFightGetlastRoundResponse(
							FightGetlastRoundResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build();
		}
		List<CardRecord> records = game.getRecords().get(game.getRecords().size() - 1);
		if (records.size() < 3) {
			return SC
					.newBuilder()
					.setFightGetlastRoundResponse(
							FightGetlastRoundResponse.newBuilder().setErrorCode(ErrorCode.FIRST_ROUND.getNumber()))
					.build();
		}
		FightGetlastRoundResponse.Builder builder = FightGetlastRoundResponse.newBuilder();
		List<CardRecord> record = new ArrayList<>();
		for (int i = (records.size() - 5 < 1 ? 0 : records.size() - 5); i < records.size() - 2; i++) {
			record.add(records.get(i));
		}
		for (CardRecord temp : record) {
			builder.addRecords(Record.newBuilder().setSeated(game.getRoleIdList().indexOf(temp.gameRoleId))
					.addAllPai(temp.cards));
		}
		return SC.newBuilder().setFightGetlastRoundResponse(builder).build();

	}

	// @Override
	// public GeneratedMessage exitGame(Role role) {
	// Game game = GameCache.getGameMap().get(role.getGameId());
	// if (game == null) {
	// return SC
	// .newBuilder()
	// .setFightExitGameResponse(
	// FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
	// .build();
	// }
	//
	// String gameRoleId = matchService.getGameRoleId(game.getGameId(),
	// role.getRoleId());
	//
	// GameState gameState = game.getGameState();
	// // 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
	// if (gameState == GameState.GAME_STATE_PREPARE && game.getRound() ==
	// game.getGameConfig().getRound()) {
	// // 若是房主，则直接解散
	// if (game.getMasterRoleId() == role.getRoleId()) {
	// if (game.getRound() == game.getGameConfig().getRound()) {
	// moneyExchangeService.exchangeMoney(role, game.getGameConfig().getRound()
	// / 3, false);
	// role.setRandiooMoney(role.getRandiooMoney() +
	// game.getGameConfig().getRound() / 3 * 10);
	// }
	// game.setGameState(GameState.GAME_START_END);
	//
	// SC scDismiss =
	// SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// SessionUtils.sc(info.roleId, scDismiss);
	// Role tempRole = (Role) RoleCache.getRoleById(info.roleId);
	// if (role != null) {
	// tempRole.setGameId(0);
	// }
	// }
	// GameCache.getGameLocks().add(game.getLockString());
	// if (!game.getLockString().equals("")) {
	// GameCache.getGameLockStringMap().remove(game.getLockString());
	// }
	// // 将游戏从缓存池中移除
	// GameCache.getGameMap().remove(game.getGameId());
	// } else {
	// // 该玩家退出
	// SC scExit = SC
	// .newBuilder()
	// .setSCFightExitGame(
	// SCFightExitGame.newBuilder().setSeated(game.getRoleIdList().indexOf(gameRoleId)))
	// .build();
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// SessionUtils.sc(info.roleId, scExit);
	// this.notifyObservers(FightConstant.FIGHT_RECORD, info, scExit);
	// }
	// game.getRoleIdMap().remove(gameRoleId);
	// role.setGameId(0);
	// }
	//
	// }
	// // 如果游戏已经开始,则要申请退出
	// else /* if (gameState == GameState.GAME_START_START) */{
	// if (game.getOnlineRoleCount() != 0 /*
	// * || game.getRoleIdMap().get(
	// * gameRoleId).applyExitTime >
	// * TimeUtils.getNowTime() - 90
	// */) {
	// return SC
	// .newBuilder()
	// .setFightExitGameResponse(
	// FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_EXITING.getNumber()))
	// .build();
	// }
	// game.setApplyExitTime(TimeUtils.getNowTime());
	// SC scApplyExit = SC
	// .newBuilder()
	// .setSCFightApplyExitGame(
	// SCFightApplyExitGame.newBuilder().setName(role.getName())
	// .setCountDown(FightConstant.COUNTDOWN)).build();
	// setOnlineCount(game.getGameId());
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// if (info.roleId != role.getRoleId()) {
	// SessionUtils.sc(info.roleId, scApplyExit);
	// this.notifyObservers(FightConstant.APPLY_LEAVE, game.getGameId(), info);
	// }
	// }
	// agreeExit(role, true);
	// }
	//
	// return
	// SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build();
	// }

	@Override
	public void exitGame(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightExitGameResponse(
									FightExitGameResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_NOT_EXIST.getNumber())).build());
			return;
		}

		if (!this.checkGameNeverStart(game)) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightExitGameResponse(
									FightExitGameResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_NOT_EXIST.getNumber())).build());
			return;
		}

		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder())
				.build());

		// 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
		// 若是房主，则直接解散
		if (game.getMasterRoleId() == role.getRoleId()) {
			// 退还钱款
			moneyExchangeService.exchangeMoney(role, game.getGameConfig().getRound() / 3, false);
			role.setRandiooMoney(role.getRandiooMoney() + game.getGameConfig().getRound() / 3 * 10);

			// 标记比赛结束
			game.setGameState(GameState.GAME_START_END);

			// 通知所有人比赛结束，并把游戏id标记变成0
			SC scDismiss = SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				SessionUtils.sc(info.roleId, scDismiss);
				Role tempRole = (Role) RoleCache.getRoleById(info.roleId);
				if (tempRole != null) {
					tempRole.setGameId(0);
				}
			}

			GameCache.getGameLocks().add(game.getLockString());
			if (!StringUtils.isNullOrEmpty(game.getLockString()))
				GameCache.getGameLockStringMap().remove(game.getLockString());

			// 将游戏从缓存池中移除
			GameCache.getGameMap().remove(game.getGameId());
		} else {
			String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
			// 该玩家直接退出
			SC scExit = SC
					.newBuilder()
					.setSCFightExitGame(
							SCFightExitGame.newBuilder().setSeated(game.getRoleIdList().indexOf(gameRoleId))).build();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				SessionUtils.sc(info.roleId, scExit);
				this.notifyObservers(FightConstant.FIGHT_RECORD, info, scExit);
			}
			game.getRoleIdMap().remove(gameRoleId);
			role.setGameId(0);
		}

	}

	@Override
	public void applyExitGame(Role role) {
		Game game = this.getGameById(role.getGameId());
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightApplyExitGameResponse(
									FightApplyExitGameResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_NOT_EXIST.getNumber())).build());
			return;
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

		// 1.距离上次拒绝时间到现在的间隔时间内不能连续发起申请退出
		// 2.有人在申请退出时不能发布自己的申请退出
		int deltaTime = 30;
		int nowTime = TimeUtils.getNowTime();

		// 是否允许申请退出
		if (!isAllowApplyExit(nowTime, game, gameRoleId, deltaTime)) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightApplyExitGameResponse(
									FightApplyExitGameResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_EXITING.getNumber())).build());
			return;
		}

		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setFightApplyExitGameResponse(FightApplyExitGameResponse.newBuilder()).build());

		// 重置投票数据
		this.resetVoteData(game);
		// 设置申请退出的玩家id
		game.setApplyExitGameRoleId(gameRoleId);

		SC scApplyExit = SC
				.newBuilder()
				.setSCFightApplyExitGame(
						SCFightApplyExitGame.newBuilder().setName(role.getName()).setApplyExitId(game.getApplyExitId())
								.setCountDown(FightConstant.COUNTDOWN)).build();

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.roleId == role.getRoleId()) {
				continue;
			}
			SessionUtils.sc(info.roleId, scApplyExit);
			this.notifyObservers(FightConstant.APPLY_LEAVE, game.getGameId(), info.gameRoleId);
		}

	}

	/**
	 * 
	 * @param game
	 * @param roleGameInfo
	 * @author wcy 2017年6月29日
	 */
	private void aiAgreeExit(int gameId, String gameRoleId) {
		Game game = this.getGameById(gameId);
		if (game == null) {
			return;
		}
		this.voteApplyExit(game, gameRoleId, game.getApplyExitId(), FightVoteApplyExit.VOTE_AGREE);
	}

	/**
	 * 
	 * @param game
	 * @author wcy 2017年6月29日
	 */
	private void resetVoteData(Game game) {
		// 所有玩家表决状态重置
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			roleGameInfo.vote = FightVoteApplyExit.VOTE_IDLE;
		}

		game.getVoteMap().clear();
		game.setApplyExitGameRoleId(null);
	}

	/**
	 * 自动生成申请退出标识符
	 * 
	 * @param game
	 * @author wcy 2017年6月29日
	 */
	private void generateApplyExitId(Game game) {
		game.setApplyExitId(game.getApplyExitId() + 1);
	}

	/**
	 * 检查游戏是否从未开始过
	 * 
	 * @param game
	 * @return
	 * @author wcy 2017年6月29日
	 */
	private boolean checkGameNeverStart(Game game) {
		GameState gameState = game.getGameState();
		GameConfig gameConfig = game.getGameConfig();
		int currentRound = game.getRound();
		int maxRound = gameConfig.getRound();
		return gameState == GameState.GAME_STATE_PREPARE && currentRound == maxRound;
	}

	/**
	 * 检查是否允许申请退出
	 * 
	 * @param game
	 * @param applyExitRoleGameId
	 * @param deltaTime
	 * @return
	 * @author wcy 2017年6月29日
	 */
	private boolean isAllowApplyExit(int nowTime, Game game, String applyExitRoleGameId, int deltaTime) {
		// 有人在申请退出时，不能让另一个人申请退出
		if (!StringUtils.isNullOrEmpty(game.getApplyExitGameRoleId()))
			return false;

		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(applyExitRoleGameId);
		int lastRejectExitTime = roleGameInfo.lastRejectExitTime;

		// 现在的时间与上次被拒绝的时间差不能小于规定间隔
		if (nowTime - lastRejectExitTime <= deltaTime)
			return false;

		return false;
	}

	@Override
	public GeneratedMessage agreeExit(Role role, FightVoteApplyExit vote, int voteId) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			return SC
					.newBuilder()
					.setFightAgreeExitGameResponse(
							FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build();
		}

		String roleInfoStr = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
		RoleGameInfo exitRoleGameInfo = game.getRoleIdMap().get(roleInfoStr);
		SessionUtils.sc(exitRoleGameInfo.roleId,
				SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build());
		this.voteApplyExit(game, roleInfoStr, voteId, vote);
		// TODO
		return null;
		// return agreeExit(game.getGameId(), roleInfoStr, agree);

	}

	// public GeneratedMessage agreeExit(int gameId, String exitGameRoleId,
	// boolean agree) {
	// Game game = GameCache.getGameMap().get(gameId);
	// RoleGameInfo roleInfo = game.getRoleIdMap().get(exitGameRoleId);
	// synchronized (game) {
	// roleInfo.agreeLeave = agree;
	// if (game.getOnlineRoleCount() == 0) {
	// return
	// SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build();
	// }
	// int flag = 0;
	// RoleGameInfo rejectRoleGameInfo = null;
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// if (info.agreeLeave != null && info.agreeLeave == false) {
	// flag = -1;
	// game.setOnlineRoleCount(0);
	// rejectRoleGameInfo = info;
	// break;
	// }
	// if (info.agreeLeave != null && info.agreeLeave) {
	// flag += 1;
	// }
	// }
	// if (flag == -1) {
	// for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
	// SessionUtils.sc(
	// roleGameInfo.roleId,
	// SC.newBuilder()
	// .setSCAgreeExitGame(
	// SCAgreeExitGame.newBuilder().setName(
	// rejectRoleGameInfo.roleId == 0 ? "ROBOT"
	// + rejectRoleGameInfo.gameRoleId : RoleCache.getRoleById(
	// rejectRoleGameInfo.roleId).getName())).build());
	// }
	// return SC
	// .newBuilder()
	// .setFightAgreeExitGameResponse(
	// FightAgreeExitGameResponse.newBuilder()
	// .setErrorCode(ErrorCode.APPLY_REJECT.getNumber())).build();
	// } else if (flag == game.getOnlineRoleCount()) {
	// Role host = (Role)
	// RoleCache.getRoleById(game.getRoleIdMap().get(game.getRoleIdList().get(0)).roleId);
	// if (game.getGameState() == GameState.GAME_START_START
	// && game.getRound() == game.getGameConfig().getRound() - 1) {
	// moneyExchangeService.exchangeMoney(host, game.getGameConfig().getRound()
	// / 3, false);
	// host.setRandiooMoney(host.getRandiooMoney() +
	// game.getGameConfig().getRound() / 3 * 10);
	// }
	// game.setGameState(GameState.GAME_START_END);
	// SCFightGameOver.Builder SCGameOverBuilder = SCFightGameOver.newBuilder();
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// GameOverData.Builder gameOverBuilder = GameOverData.newBuilder();
	// Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
	// String name = "";
	// String headImgUrl = "ui://h24q1ml0x7tz13m";
	// if (role == null) {
	// name = "ROBOT" + info.gameRoleId;
	// } else {
	// name = role.getName();
	// headImgUrl = role.getHeadImgUrl();
	// }
	// gameOverBuilder.setFarmerNum(info.farmerNum);
	// gameOverBuilder.setLandLordNum(info.landLordNum);
	// gameOverBuilder.setName(name);
	// gameOverBuilder.setScore(info.allMark);
	// gameOverBuilder.setSeated(game.getRoleIdList().indexOf(info.gameRoleId));
	// gameOverBuilder.setHeadImgUrl(headImgUrl);
	// gameOverBuilder.setRoleId(info.roleId);
	// SCGameOverBuilder.addGameOverData(gameOverBuilder);
	// }
	// SC gameOverSC =
	// SC.newBuilder().setSCFightGameOver(SCGameOverBuilder).build();
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// if (info.roleId == 0) {
	// continue;
	// }
	// if (SessionCache.getSessionById(info.roleId) == null
	// || SessionCache.getSessionById(info.roleId).isClosing()) {
	// Role role = (Role) RoleCache.getRoleById(info.roleId);
	// if (role != null) {
	// role.setSc(gameOverSC);
	// }
	// info.videoRoundPoint.add(info.scList.size());
	// this.notifyObservers(FightConstant.FIGHT_RECORD, info,
	// gameOverSC.toBuilder().clone().build());
	// List<ByteString> list = new ArrayList<>(info.scList.size());
	// for (SC sc : info.scList) {
	// list.add(sc.toByteString());
	// }
	// List<ByteString> gameOverList = new
	// ArrayList<>(game.getRoundoverList().size());
	// for (SC sc : game.getRoundoverList()) {
	// gameOverList.add(sc.toByteString());
	// }
	// video videodata = video.newBuilder().addAllVideoRecord(list)
	// .addAllKeyPoint(info.videoRoundPoint).addAllRoundOver(gameOverList)
	// .addAllStartTime(game.getStartTime()).setGameOver(gameOverSC.toByteString())
	// .setGameType(game.getGameType().getNumber())
	// .setRoomId(game.getLockString().equals("") ? "0" :
	// game.getLockString()).build();
	// videoDao.insert(VideoUtils.toVideo(info, videodata));
	// // TODO
	// role.setGameId(0);
	// continue;
	// }
	// info.videoRoundPoint.add(info.scList.size());
	// SessionUtils.sc(info.roleId,
	// SC.newBuilder().setSCFightAllAgreeExit(SCFightAllAgreeExit.newBuilder()).build());
	// SessionUtils.sc(info.roleId, gameOverSC);
	// this.notifyObservers(FightConstant.FIGHT_RECORD, info,
	// gameOverSC.toBuilder().clone().build());
	// List<ByteString> list = new ArrayList<>(info.scList.size());
	// for (SC sc : info.scList) {
	// list.add(sc.toByteString());
	// }
	// List<ByteString> gameOverList = new
	// ArrayList<>(game.getRoundoverList().size());
	// for (SC sc : game.getRoundoverList()) {
	// gameOverList.add(sc.toByteString());
	// }
	// video videodata =
	// video.newBuilder().addAllVideoRecord(list).addAllKeyPoint(info.videoRoundPoint)
	// .addAllRoundOver(gameOverList).addAllStartTime(game.getStartTime())
	// .setGameOver(gameOverSC.toByteString()).setGameType(game.getGameType().getNumber())
	// .setRoomId(game.getLockString().equals("") ? "0" :
	// game.getLockString()).build();
	// videoDao.insert(VideoUtils.toVideo(info, videodata));
	// // TODO
	// Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
	// role.setGameId(0);
	// }
	//
	// // 将游戏从缓存池中移除
	// GameCache.getGameLocks().add(game.getLockString());
	// if (!game.getLockString().equals("")) {
	// GameCache.getGameLockStringMap().remove(game.getLockString());
	// }
	// GameCache.getGameMap().remove(game.getGameId());
	// }
	// }
	//
	// return
	// SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build();
	// }

	private void voteApplyExit(Game game, String voteGameRoleId, int applyExitId, FightVoteApplyExit vote) {
		// 如果申请号不一样则直接返回不处理
		if (game.getApplyExitId() != applyExitId) {
			return;
		}

		// 有人拒绝则直接取消申请
		if (vote == FightVoteApplyExit.VOTE_REJECT) {
			this.resetVoteData(game);
			this.generateApplyExitId(game);
		} else {
			Map<String, FightVoteApplyExit> voteMap = game.getVoteMap();
			voteMap.put(voteGameRoleId, vote);
			// 检查人数是否满足,不满足查连接状态
			if (voteMap.size() == (game.getRoleIdMap().size() - 1)) {
				// 游戏结束
				this.sendSC(game, SC.newBuilder().setSCFightAllAgreeExit(SCFightAllAgreeExit.newBuilder()).build());
				this.cancelGame(game);
			} else {
				WAIT_VOTE: {
					for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
						// 申请人就跳过
						if (roleGameInfo.gameRoleId.equals(game.getApplyExitGameRoleId())) {
							continue;
						}
						// 投票中没有此人就检查连接,没断就返回
						if (!game.getVoteMap().containsKey(roleGameInfo.gameRoleId)) {
							IoSession session = SessionCache.getSessionById(roleGameInfo.roleId);
							if (!session.isClosing()) {
								break WAIT_VOTE;
							}
						}
					}

					// 游戏结束
					this.sendSC(game, SC.newBuilder().setSCFightAllAgreeExit(SCFightAllAgreeExit.newBuilder()).build());
					this.cancelGame(game);
				}

			}
		}

	}

	/**
	 * 取消游戏
	 * 
	 * @param game
	 * @author wcy 2017年6月29日
	 */
	private void cancelGame(Game game) {
		game.setGameState(GameState.GAME_START_END);
		SCFightGameOver.Builder scFightGameOverBuilder = SCFightGameOver.newBuilder();
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			scFightGameOverBuilder.addGameOverData(parseGameOverDate(game, roleGameInfo));
		}

		this.sendSC(game, SC.newBuilder().setSCFightGameOver(scFightGameOverBuilder).build());
		// 将游戏从缓存池中移除
		GameCache.getGameLocks().add(game.getLockString());
		if (!game.getLockString().equals("")) {
			GameCache.getGameLockStringMap().remove(game.getLockString());
		}
		GameCache.getGameMap().remove(game.getGameId());
	}

	private GameOverData parseGameOverDate(Game game, RoleGameInfo info) {
		GameOverData.Builder gameOverBuilder = GameOverData.newBuilder();
		Role role = (Role) RoleCache.getRoleById(info.roleId);
		String name = role == null ? "ROBOT" + info.gameRoleId : role.getName();
		String headImgUrl = role == null ? "ui://h24q1ml0x7tz13m" : role.getHeadImgUrl();

		gameOverBuilder.setFarmerNum(info.farmerNum);
		gameOverBuilder.setLandLordNum(info.landLordNum);
		gameOverBuilder.setName(name);
		gameOverBuilder.setScore(info.allMark);
		gameOverBuilder.setSeated(game.getRoleIdList().indexOf(info.gameRoleId));
		gameOverBuilder.setHeadImgUrl(headImgUrl);
		gameOverBuilder.setRoleId(info.roleId);
		return gameOverBuilder.build();
	}

	@Override
	public void dispatchCard(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		game.setMoGuai(false);
		for (RoleGameInfo info : game.getRoleIdMap().values())
			info.cards.clear();

		int maxCount = game.getMaxRoleCount();
		int needCard = 1;
		int totalCardCount = (FightConstant.CARDS.length - maxCount) / maxCount;
		int landlordCardBoxIndex = RandomUtils.getRandomNum(needCard);
		int landlordCardIndex = RandomUtils.getRandomNum(FightConstant.CARDS.length);
		/*
		 * int [][]card = { {0x11, 0x21, 0x31, 0x41, 0x12, 0x22, 0x32, 0x42,
		 * 0x13, 0x23, 0x33, 0x43, 0x14, 0x24, 0x34, 0x44, 0x1D}, {0x15, 0x25,
		 * 0x35, 0x49, 0x16, 0x26, 0x36, 0x4A, 0x17, 0x27, 0x3B, 0x4B, 0x18,
		 * 0x28, 0x38, 0x48, 0x4C}, {0x19, 0x29, 0x39, 0x45, 0x1A, 0x2A, 0x3A,
		 * 0x46, 0x1B, 0x2B, 0x37, 0x47, 0x1C, 0x2C, 0x3C, 0x0E, 0x0F} }; int
		 * []landlord = {0x2D, 0x3D, 0x4D};
		 */
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

					// 如果符合条件,就从这个人开始叫地主
					if (landlordCardBoxIndex == j && landlordCardIndex == index) {
						// 如果明牌是大小王，则要翻倍
						if ((value == CardTools.C_KING || value == CardTools.C_QUEUE)
								&& (game.getGameConfig().getMoguai())) {
							game.setMoGuai(true);
							game.setMultiple(game.getMultiple() + 1);
						}

						// 设置开始叫地主的人的索引
						game.setCurrentRoleIdIndex(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId));
					}

					roleGameInfo.cards.add(value);
				}
			}

			// 对所有玩家的卡牌进行排序
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values())
				Collections.sort(roleGameInfo.cards, hexCardComparator);

			// 剩下的牌是地主牌
			game.getLandlordCards().addAll(list);
		}
		/*
		 * for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()){
		 * for(int t :
		 * card[game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)])
		 * roleGameInfo.cards.add(t); } game.setCurrentRoleIdIndex(0); for(int t
		 * : landlord) game.getLandlordCards().add(t);
		 */
	}

	@Override
	public void callLandlord(Role role, int fen) {
		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightCallLandLordResponse(
									FightCallLandLordResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_NOT_EXIST.getNumber())).build());
		}
		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setFightCallLandLordResponse(FightCallLandLordResponse.newBuilder()).build());
		// 叫地主
		this.callLandlord(gameId, gameRoleId, fen);

	}

	@Override
	public GeneratedMessage recommandCardList(Role role) {
		int gameId = role.getGameId();
		if (role.getRoleId() != this.getCurrentRoleGameInfo(gameId).roleId) {
			return SC
					.newBuilder()
					.setFightRecommandResponse(
							FightRecommandResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
					.build();

		}
		List<List<Integer>> list = this.getRecommandCardList(gameId);
		FightRecommandResponse.Builder builder = FightRecommandResponse.newBuilder();
		for (List<Integer> temp : list) {
			builder.addRecommandPai(RecommandPai.newBuilder().addAllPai(temp));
		}
		return SC.newBuilder().setFightRecommandResponse(builder).build();
	}

	/**
	 * 获得推荐牌
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月2日
	 */
	private List<List<Integer>> getRecommandCardList(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		CardList lastCardList = game.getLastCardList();
		RoleGameInfo roleGameInfo = this.getCurrentRoleGameInfo(gameId);

		CardSort cardSort = new CardSort();
		CardTools.fillCardSort(cardSort, roleGameInfo.cards);
		List<List<Integer>> recommandList = new ArrayList<>();
		// 先检查牌型，如果手牌成牌型，并且能出
		if (lastCardList == null) {
			for (CardList cardList : GameCache.getCardLists().values()) {
				CardList sendCardList = checkCardList(cardList, cardSort, roleGameInfo.cards);
				if (sendCardList != null
						&& (lastCardList == null || sendCardListBiggerThanLastCardList(sendCardList, lastCardList))) {
					List<Integer> tList = new ArrayList<Integer>();
					for (int t : roleGameInfo.cards) {
						tList.add(CardTools.toNum(t));
					}
					recommandList.add(tList);
					System.out.println(tList + sendCardList.getClass().getName());
					return recommandList;
				}
			}
		}
		// 手牌不成牌型
		for (CardList cardList : GameCache.getRecommendCardLists().values()) {
			cardList.recommand(recommandList, cardSort, lastCardList, roleGameInfo.cards);
		}
		System.out.println(recommandList);
		return recommandList;

	}

	@Override
	public void callLandlord(int gameId, String gameRoleId, int callScore) {
		Game game = GameCache.getGameMap().get(gameId);
		System.out.print(gameRoleId + ":");

		// 检查是否是该人叫分
		int index = game.getRoleIdList().indexOf(gameRoleId);
		if (index != game.getCurrentRoleIdIndex() || game.getCallLandlordCount() == game.getMaxRoleCount()
				|| game.getCallLandlordScore() == FightConstant.SCORE_3) {
			// 安全检查
			System.out.println("error");
			return;
		}
		game.getRoleIdMap().get(gameRoleId).callLandlordScore = callScore;
		int score = game.getCallLandlordScore();
		// 是否叫分
		boolean call = callScore != 0;
		if (call) {
			// 叫的分数不能比上一次低
			if (callScore < score) {
				System.out.println("叫分必须比上一个人高");
				return;
			}
			System.out.println("叫分:" + callScore);

			// 设置叫的分数
			game.setCallLandlordScore(callScore);
			game.setLandlordGameRoleId(gameRoleId);

			// 检查是不是叫了3分,如果叫了三分就是地主,加牌开始比赛
			if (game.getCallLandlordScore() == FightConstant.SCORE_3) {
				System.out.println("地主是:" + gameRoleId);

				giveLandlordCards(game.getGameId());
				SCFightLandLord scFightLandLord = SCFightLandLord.newBuilder()
						.addAllLandLordPai(game.getLandlordCards())
						.setSeated(game.getRoleIdList().indexOf(game.getLandlordGameRoleId())).build();

				SC sc = SC
						.newBuilder()
						.setSCFightPutOut(
								SCFightPutOut
										.newBuilder()
										.setCountdown(
												game.getRoleIdMap().get(
														game.getRoleIdList().get(game.getCurrentRoleIdIndex())).auto < 2 ? FightConstant.COUNTDOWN : 1)
										.setSeated(game.getCurrentRoleIdIndex()).setAllowGuo(false)).build();
				game.setCurrentStatusSC(sc);
				for (RoleGameInfo info : game.getRoleIdMap().values()) {
					if (!info.gameRoleId.equals(game.getLandlordGameRoleId())) {
						info.farmerNum++;
						SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightLandLord(scFightLandLord).build());
						this.notifyObservers(FightConstant.FIGHT_RECORD, info,
								SC.newBuilder().setSCFightLandLord(scFightLandLord).build());
					} else {
						info.landLordNum++;
						SessionUtils.sc(
								info.roleId,
								SC.newBuilder()
										.setSCFightLandLord(
												scFightLandLord.toBuilder().addAllPai(
														game.getRoleIdMap().get(info.gameRoleId).cards)).build());
						this.notifyObservers(
								FightConstant.FIGHT_RECORD,
								info,
								SC.newBuilder()
										.setSCFightLandLord(
												scFightLandLord.toBuilder().addAllPai(
														game.getRoleIdMap().get(info.gameRoleId).cards)).build());
						this.notifyObservers(FightConstant.FIGHT_MINGPAI, game.getGameId(), info.roleId);
					}

					SessionUtils.sc(info.roleId, sc);
					this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());
				}
				notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);

				// 开始比赛
				return;
			}

		} else {
			System.out.println("不叫");

		}

		// 叫地主计数器加1
		int callCount = game.getCallLandlordCount();
		callCount++;
		game.setCallLandlordCount(callCount);
		// 检查是否每个人都叫过了
		if (callCount >= game.getMaxRoleCount()) {
			// 如果人数到了,则那最后一个人的叫分,如果没有人叫分,则重新发牌
			int resultScore = game.getCallLandlordScore();
			if (resultScore == 0) {
				// 说明没有人叫分，重新发牌
				gameInit(gameId);
				System.out.println("没人叫地主，重新发牌");
				SCFightStart.Builder FSBuilder = SCFightStart.newBuilder();
				for (int i = 0; i < game.getMaxRoleCount(); i++) {
					FSBuilder.addPaiNum(PaiNum.newBuilder().setSeated(i)
							.setNum(game.getRoleIdMap().get(game.getRoleIdList().get(i)).cards.size()));

				}
				FSBuilder.setTimes((int) Math.pow(2, game.getMultiple()));
				FSBuilder.setRoundNum(game.getRound());
				SC sc = SC
						.newBuilder()
						.setSCFightCallLandLord(
								SCFightCallLandLord.newBuilder().setCurrentFen(0)
										.setSeated(game.getCurrentRoleIdIndex())
										.setCountdown(FightConstant.COUNTDOWN / 2).setFen(-1)).build();
				game.setCurrentStatusSC(sc);
				for (RoleGameInfo info : game.getRoleIdMap().values()) {
					System.out.println("~~~" + info.roleId + game.getMultiple());
					SessionUtils.sc(info.roleId,
							SC.newBuilder().setSCFightStart(FSBuilder.clone().addAllPai(info.cards)).build());
					this.notifyObservers(FightConstant.FIGHT_RECORD, info,
							SC.newBuilder().setSCFightStart(FSBuilder.clone().addAllPai(info.cards)).build());
					SessionUtils.sc(info.roleId, sc);
					this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());
				}
				this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
				return;
			} else {
				// 看叫分最高的是谁
				System.out.println("地主是:" + game.getLandlordGameRoleId());
				this.giveLandlordCards(gameId);
				game.setCurrentRoleIdIndex(game.getRoleIdList().indexOf(game.getLandlordGameRoleId()));
				SCFightLandLord scFightLandLord = SCFightLandLord.newBuilder()
						.addAllLandLordPai(game.getLandlordCards())
						.setSeated(game.getRoleIdList().indexOf(game.getLandlordGameRoleId())).build();

				SC sc = SC
						.newBuilder()
						.setSCFightPutOut(
								SCFightPutOut
										.newBuilder()
										.setCountdown(
												game.getRoleIdMap().get(
														game.getRoleIdList().get(game.getCurrentRoleIdIndex())).auto < 2 ? FightConstant.COUNTDOWN : 1)
										.setSeated(game.getRoleIdList().indexOf(game.getLandlordGameRoleId()))
										.setAllowGuo(false)).build();
				game.setCurrentStatusSC(sc);
				for (RoleGameInfo info : game.getRoleIdMap().values()) {
					if (!info.gameRoleId.equals(game.getLandlordGameRoleId())) {
						info.farmerNum++;
						SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightLandLord(scFightLandLord).build());
						this.notifyObservers(FightConstant.FIGHT_RECORD, info,
								SC.newBuilder().setSCFightLandLord(scFightLandLord).build());
					} else {
						info.landLordNum++;
						SessionUtils.sc(
								info.roleId,
								SC.newBuilder()
										.setSCFightLandLord(
												scFightLandLord.toBuilder().addAllPai(
														game.getRoleIdMap().get(info.gameRoleId).cards)).build());
						this.notifyObservers(
								FightConstant.FIGHT_RECORD,
								info,
								SC.newBuilder()
										.setSCFightLandLord(
												scFightLandLord.toBuilder().addAllPai(
														game.getRoleIdMap().get(info.gameRoleId).cards)).build());
						this.notifyObservers(FightConstant.FIGHT_MINGPAI, game.getGameId(), info.roleId);
					}

					SessionUtils.sc(info.roleId, sc);
					this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());
				}
				notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
			}
		} else {
			// 如果人数没有到,则通知下一个人
			System.out.println("22222---" + callCount);
			int nextIndex = this.getNextIndex(gameId);
			String nextRoleGameId = game.getRoleIdList().get(nextIndex);
			game.setCallLandlordCount(callCount);
			SC sc = SC
					.newBuilder()
					.setSCFightCallLandLord(
							SCFightCallLandLord.newBuilder().setCurrentFen(game.getCallLandlordScore())
									.setSeated(nextIndex).setCountdown(FightConstant.COUNTDOWN / 2).setFen(callScore))
					.build();
			game.setCurrentStatusSC(sc);
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				SessionUtils.sc(info.roleId, sc);
				this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());
			}
			this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
			System.out.println("通知下一个人:" + nextRoleGameId);

		}

	}

	/**
	 * 给地主牌
	 * 
	 * @param gameId
	 * @author wcy 2017年6月1日
	 */
	private void giveLandlordCards(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		String landlordId = game.getLandlordGameRoleId();
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(landlordId);
		roleGameInfo.cards.addAll(game.getLandlordCards());
	}

	@Override
	public void sendCard(Role role, List<Integer> paiList) {
		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightSendCardResponse(
									FightSendCardResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_NOT_EXIST.getNumber())).build());
			return;
		}
		String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		if (roleGameInfo.roleId != role.getRoleId()) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightSendCardResponse(
									FightSendCardResponse.newBuilder()
											.setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber())).build());
			return;
		}
		roleGameInfo.auto = 0;
		if (game.getGameType() == GameType.GAME_TYPE_MATCH) {
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				SessionUtils.sc(
						info.roleId,
						SC.newBuilder()
								.setSCFightAuto(
										SCFightAuto.newBuilder()
												.setSeated(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId))
												.setAuto(false)).build());
			}
		}
		this.gameRoleIdSendCard(paiList, gameId, gameRoleId);
	}

	private void gameRoleIdSendCard(List<Integer> paiList, int gameId, String gameRoleId) {
		Game game = GameCache.getGameMap().get(gameId);
		int roleId = game.getRoleIdMap().get(gameRoleId).roleId;

		CardList lastCardList = game.getLastCardList();

		CardList sendCardList = null;

		// 如果长度是0，说明不出牌，则要检查是否允许不出牌
		if (paiList.size() == 0) {
			if (game.getPassCount() >= (game.getMaxRoleCount() - 1)) {
				// 不允许不出牌
				SessionUtils.sc(
						roleId,
						SC.newBuilder()
								.setFightSendCardResponse(
										FightSendCardResponse.newBuilder().setErrorCode(
												ErrorCode.NULL_REJECT.getNumber())).build());
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
			sendCardList = lastCardList == null ? initiativeSend(cardSort, paiList) : passiveSend(
					lastCardList.getClass(), cardSort, paiList);

			// 匹配牌型失败
			if (sendCardList == null) {
				SessionUtils.sc(
						roleId,
						SC.newBuilder()
								.setFightSendCardResponse(
										FightSendCardResponse.newBuilder().setErrorCode(
												ErrorCode.NOT_SAME_TYPE.getNumber())).build());
				return;
			}

			// 比较大小
			if (lastCardList != null) {
				try {
					boolean bigger = sendCardListBiggerThanLastCardList(sendCardList, lastCardList);
					if (!bigger) {
						SessionUtils.sc(
								roleId,
								SC.newBuilder()
										.setFightSendCardResponse(
												FightSendCardResponse.newBuilder().setErrorCode(
														ErrorCode.SMALLER.getNumber())).build());
						return;
					}
				} catch (CardTypeComparableException e) {
					SessionUtils.sc(
							roleId,
							SC.newBuilder()
									.setFightSendCardResponse(
											FightSendCardResponse.newBuilder().setErrorCode(
													ErrorCode.NOT_SAME_TYPE.getNumber())).build());
					return;
				}
			}
		}
		// 设置最后一个人的牌型
		sendCard(paiList, gameId, gameRoleId, sendCardList);
		boolean allowGuo = game.getPassCount() < (game.getMaxRoleCount() - 1);
		SessionUtils.sc(roleId, SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());
		/**
		 * 通知已经出牌
		 */

		SCFightPutOutPai.Builder SCPutOutBuilder = SCFightPutOutPai.newBuilder().addAllPutOutPai(paiList)
				.setSeated(game.getCurrentRoleIdIndex()).setTimes((int) Math.pow(2, game.getMultiple()))
				.setType(sendCardList == null ? "guo" : sendCardList.getClass().getSimpleName());
		for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {

			SCPutOutBuilder.addPaiNum(PaiNum.newBuilder().setSeated(game.getRoleIdList().indexOf(tInfo.gameRoleId))
					.setNum(tInfo.cards.size()));

		}
		SCFightPutOutPai sCFightPutOutPai = SCPutOutBuilder.build();
		for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
			SessionUtils.sc(tInfo.roleId, SC.newBuilder().setSCFightPutOutPai(sCFightPutOutPai).build());
			this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo,
					SC.newBuilder().setSCFightPutOutPai(sCFightPutOutPai).build());
			if (gameRoleId.equals(game.getLandlordGameRoleId()) && game.isMingPaiState()) {
				RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
				SessionUtils.sc(
						tInfo.roleId,
						SC.newBuilder()
								.setSCFightMingPai(
										SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
												.setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId))).build());
				this.notifyObservers(
						FightConstant.FIGHT_RECORD,
						tInfo,
						SC.newBuilder()
								.setSCFightMingPai(
										SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
												.setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId))).build());
			}
			if (game.getRecords().get(game.getRecords().size() - 1).size() == 3) {
				SessionUtils.sc(tInfo.roleId,
						SC.newBuilder().setSCFightLastRoundReady(SCFightLastRoundReady.newBuilder()).build());
				this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo,
						SC.newBuilder().setSCFightLastRoundReady(SCFightLastRoundReady.newBuilder()).build());
			}
		}
		notifyObservers(FightConstant.SEND_CARD, gameId, gameRoleId, sendCardList);

		// 检查游戏是否结束
		if (!this.checkGameOver(gameId)) {
			// 如果没有结束，则请求下一个人出牌
			// 将索引换到下一个人
			getNextIndex(gameId);

			game.setSendCardCount(game.getSendCardCount() + 1);
			game.setSendCardTime(TimeUtils.getNowTime());

			SC sc = SC
					.newBuilder()
					.setSCFightPutOut(
							SCFightPutOut
									.newBuilder()
									.setCountdown(
											game.getRoleIdMap().get(
													game.getRoleIdList().get(game.getCurrentRoleIdIndex())).auto < 2 ? FightConstant.COUNTDOWN : 1)
									.setSeated(game.getCurrentRoleIdIndex()).setAllowGuo(allowGuo)).build();
			game.setCurrentStatusSC(sc);
			for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
				SessionUtils.sc(tInfo.roleId, sc);
				this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo, sc.toBuilder().clone().build());
			}

			notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
		}
	}

	public void sendCard(List<Integer> paiList, int gameId, String gameRoleId, CardList sendCardList) {

		Game game = GameCache.getGameMap().get(gameId);
		synchronized (game) {
			CardRecord record = new CardRecord();
			record.cards = paiList;
			record.gameRoleId = gameRoleId;
			game.getRecords().get(game.getRecords().size() - 1).add(record);
			// 如果出牌了，则放弃出牌的计数器重置
			if (sendCardList != null) {
				game.setPassCount(0);
				if (sendCardList.getClass() == A4.class || sendCardList.getClass() == KQ.class) {
					game.setBomb(game.getBomb() + 1);
					game.setMultiple(game.getMultiple() + 1);
				}
				if (game.isLandLordSpring() && !gameRoleId.equals(game.getLandlordGameRoleId())) {
					System.out.println("dizhuSpring!~");
					game.setLandLordSpring(false);
				}
				if (game.getRecords().get(game.getRecords().size() - 1).size() > 1
						&& gameRoleId.equals(game.getLandlordGameRoleId())) {
					game.setFarmerSpring(false);
					System.out.println("farmerSpring!~");
				}
				game.setLastCardList(sendCardList);
			} else {
				if (game.getPassCount() >= game.getMaxRoleCount() - 1) {
					game.setLastCardList(null);
				}
			}
			// 从手牌中移除该牌
			RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
			for (int pai : paiList) {
				int index = info.cards.indexOf(pai);
				System.out.println(info.cards + "~~~" + pai + "---" + index);
				info.cards.remove(index);
			}
		}
	}

	private Game getGameById(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		return game;
	}

	private void gameOver(Game game, String gameRoleId) {
		scoreService.updateScore(game.getGameId(), gameRoleId);
		game.setGameState(GameState.GAME_STATE_PREPARE);
		SCFightRoundOver.Builder builder = SCFightRoundOver.newBuilder();
		builder.setOver(game.getRound() == 0);
		builder.setDang(game.getCallLandlordScore());
		builder.setAll(game.getCallLandlordScore() * game.getGameConfig().getDi());
		builder.setBomb(game.getBomb());
		builder.setMingpai(game.isMingPaiState());
		builder.setMoguai(game.isMoGuai());
		System.out.println("dizhuWin:" + gameRoleId.equals(game.getLandlordGameRoleId()) + "-dizhuSpring:"
				+ game.isLandLordSpring() + game.isFarmerSpring());
		builder.setSpring((gameRoleId.equals(game.getLandlordGameRoleId()) && game.isLandLordSpring())
				|| (!gameRoleId.equals(game.getLandlordGameRoleId()) && game.isFarmerSpring()));
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
			String name = "";
			int money = 0;
			if (role == null) {
				name = "ROBOT" + info.gameRoleId;
			} else {
				info.ready = false;
				name = role.getName();
				money = role.getMoney();
			}
			info.callLandlordScore = -1;

			builder.addScore(Score.newBuilder().setName(name).setScore(info.currentMark).setAllScore(info.allMark)
					.setSeated(game.getRoleIdList().indexOf(info.gameRoleId)).setMoney(money).addAllPai(info.cards));
		}
		SC sc = SC.newBuilder().setSCFightRoundOver(builder).build();
		game.setCurrentStatusSC(sc);
		game.getRoundoverList().add(sc);
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			SessionUtils.sc(info.roleId, sc);
			this.notifyObservers(FightConstant.FIGHT_RECORD, info, sc.toBuilder().clone().build());
			info.videoRoundPoint.add(info.scList.size());
		}
		if (game.getRound() == 0) {
			SCFightGameOver.Builder SCGameOverBuilder = SCFightGameOver.newBuilder();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				GameOverData.Builder gameOverBuilder = GameOverData.newBuilder();
				Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
				String name = "";
				String headImgUrl = "ui://h24q1ml0x7tz13m";
				if (role == null) {
					name = "ROBOT" + info.gameRoleId;
				} else {
					name = role.getName();
					headImgUrl = role.getHeadImgUrl();
				}
				gameOverBuilder.setFarmerNum(info.farmerNum);
				gameOverBuilder.setLandLordNum(info.landLordNum);
				gameOverBuilder.setName(name);
				gameOverBuilder.setScore(info.allMark);
				gameOverBuilder.setSeated(game.getRoleIdList().indexOf(info.gameRoleId));
				gameOverBuilder.setHeadImgUrl(headImgUrl);
				gameOverBuilder.setRoleId(info.roleId);
				SCGameOverBuilder.addGameOverData(gameOverBuilder);
			}
			SC gameOverSC = SC.newBuilder().setSCFightGameOver(SCGameOverBuilder).build();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				if (info.roleId == 0) {
					continue;
				}
				if (SessionCache.getSessionById(info.roleId) == null
						|| SessionCache.getSessionById(info.roleId).isClosing()) {
					Role role = (Role) RoleCache.getRoleById(info.roleId);
					if (role != null) {
						role.setSc(gameOverSC);
					}
					role.setGameId(0);
					this.notifyObservers(FightConstant.FIGHT_RECORD, info, gameOverSC.toBuilder().clone().build());
					List<ByteString> list = new ArrayList<>(info.scList.size());
					for (SC tempSc : info.scList) {
						list.add(tempSc.toByteString());
					}
					List<ByteString> gameOverList = new ArrayList<>(game.getRoundoverList().size());
					for (SC tempSc : game.getRoundoverList()) {
						gameOverList.add(tempSc.toByteString());
					}
					video videodata = video.newBuilder().addAllVideoRecord(list).addAllKeyPoint(info.videoRoundPoint)
							.addAllRoundOver(gameOverList).addAllStartTime(game.getStartTime())
							.setGameOver(gameOverSC.toByteString()).setGameType(game.getGameType().getNumber())
							.setRoomId(game.getLockString().equals("") ? "0" : game.getLockString()).build();
					videoDao.insert(VideoUtils.toVideo(info, videodata));
					// TODO
					continue;
				}
				SessionUtils.sc(info.roleId, gameOverSC);
				this.notifyObservers(FightConstant.FIGHT_RECORD, info, gameOverSC.toBuilder().clone().build());
				List<ByteString> list = new ArrayList<>(info.scList.size());
				for (SC tempSc : info.scList) {
					list.add(tempSc.toByteString());
				}
				List<ByteString> gameOverList = new ArrayList<>(game.getRoundoverList().size());
				for (SC tempSc : game.getRoundoverList()) {
					gameOverList.add(tempSc.toByteString());
				}
				video videodata = video.newBuilder().addAllVideoRecord(list).addAllKeyPoint(info.videoRoundPoint)
						.addAllRoundOver(gameOverList).addAllStartTime(game.getStartTime())
						.setGameOver(gameOverSC.toByteString()).setGameType(game.getGameType().getNumber())
						.setRoomId(game.getLockString().equals("") ? "0" : game.getLockString()).build();
				videoDao.insert(VideoUtils.toVideo(info, videodata));
				// TODO
				// SessionUtils.sc(info.roleId,
				// SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build());
				Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
				role.setGameId(0);
			}
			GameCache.getGameLocks().add(game.getLockString());
			if (!game.getLockString().equals("")) {
				GameCache.getGameLockStringMap().remove(game.getLockString());
			}
			GameCache.getGameMap().remove(game.getGameId());
		}

	}

	private void checkAutoAI(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);

		// 发送等待消息
		RoleGameInfo info = getCurrentRoleGameInfo(gameId);
		if (info.auto >= 2) {
			SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

				@Override
				public void update(TimeEvent timeEvent) {
					timeUp((SendCardTimeEvent) timeEvent);
				}
			};

			sendCardTimeEvent.setRound(game.getRecords().get(game.getRecords().size() - 1).size());
			sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + 1);
			sendCardTimeEvent.setGameId(gameId);
			sendCardTimeEvent.setFirst(false);
			sendCardTimeEvent.setMustPutOut(true);

			eventScheduler.addEvent(sendCardTimeEvent);
			return;
		}
		if (game.getGameType() == GameType.GAME_TYPE_FRIEND && info.roleId != 0) {
			return;
		}

		int waitTime = info.roleId == 0 ? 2 : FightConstant.SEND_CARD_WAIT_TIME;
		SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

			@Override
			public void update(TimeEvent timeEvent) {
				timeUp((SendCardTimeEvent) timeEvent);
			}
		};

		sendCardTimeEvent.setRound(game.getRecords().get(game.getRecords().size() - 1).size());
		sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + waitTime/*
																	 * FightConstant
																	 * .
																	 * SEND_CARD_WAIT_TIME
																	 */);
		sendCardTimeEvent.setGameId(gameId);
		sendCardTimeEvent.setFirst(true);
		sendCardTimeEvent.setMustPutOut(false);

		eventScheduler.addEvent(sendCardTimeEvent);
	}

	private void timeUp(SendCardTimeEvent event) {
		int gameId = event.getGameId();
		Game game = GameCache.getGameMap().get(gameId);
		if (game == null) {
			return;
		}
		// 如果出牌数已经改变,或者游戏已经结束,则直接返回
		String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
		if (game.getRecords().get(game.getRecords().size() - 1).size() != event.getRound())
			return;
		if (game.getRoleIdMap().get(gameRoleId).online || !event.isFirst()) {
			if (game.getRoleIdMap().get(gameRoleId).online && !event.isFirst() && !event.isMustPutOut()) {
				SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

					@Override
					public void update(TimeEvent timeEvent) {
						timeUp((SendCardTimeEvent) timeEvent);
					}
				};

				sendCardTimeEvent.setRound(game.getRecords().get(game.getRecords().size() - 1).size());

				sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.SEND_CARD_WAIT_TIME);
				sendCardTimeEvent.setGameId(gameId);
				sendCardTimeEvent.setFirst(false);
				sendCardTimeEvent.setMustPutOut(true);
				eventScheduler.addEvent(sendCardTimeEvent);
				return;
			}
			this.autoSendCard(gameId, gameRoleId);
		} else {
			SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

				@Override
				public void update(TimeEvent timeEvent) {
					timeUp((SendCardTimeEvent) timeEvent);
				}
			};

			sendCardTimeEvent.setRound(game.getRecords().get(game.getRecords().size() - 1).size());
			sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.SEND_CARD_WAIT_TIME * 2);
			sendCardTimeEvent.setGameId(gameId);
			sendCardTimeEvent.setFirst(false);
			sendCardTimeEvent.setMustPutOut(false);
			eventScheduler.addEvent(sendCardTimeEvent);
			return;
		}

		System.out.println("time up");
	}

	private void timeUp(CallLandLordTimeEvent event) {
		int gameId = event.getGameId();
		int t = event.getScore();
		int currentScore = GameCache.getGameMap().get(gameId).getCallLandlordScore();
		callLandlord(gameId, event.getGameRoleId(), t > currentScore ? t : 0);
	}

	@Override
	public void disconnectTimeUp(int roleId) {
		DisconnectTimeEvent disconnectTimeEvent = new DisconnectTimeEvent() {

			@Override
			public void update(TimeEvent timeEvent) {
				int roleId = ((DisconnectTimeEvent) timeEvent).getRoleId();
				if (SessionCache.getSessionById(roleId) == null || SessionCache.getSessionById(roleId).isClosing()) {
					Role role = (Role) RoleCache.getRoleMap().get(roleId);
					Game game = GameCache.getGameMap().get(role.getGameId());
					if (game != null && game.getGameState() == GameState.GAME_STATE_PREPARE) {
						exitGame(role);
					}
				}
			}
		};
		disconnectTimeEvent.setEndTime(TimeUtils.getNowTime() + 120/*
																	 * FightConstant
																	 * .
																	 * SEND_CARD_WAIT_TIME
																	 */);
		disconnectTimeEvent.setRoleId(roleId);

		eventScheduler.addEvent(disconnectTimeEvent);

	}

	/**
	 * 自动出牌
	 * 
	 * @param gameId
	 * @param gameRoleId
	 * @author wcy 2017年6月2日
	 */
	private void autoSendCard(int gameId, String gameRoleId) {
		// 否则进行自动出牌
		List<Integer> paiList = this.getAutoPaiList(gameId);
		List<Integer> cards = new ArrayList<>();
		List<Integer> putOut = new ArrayList<>();
		if (paiList != null) {
			cards.addAll(GameCache.getGameMap().get(gameId).getRoleIdMap().get(gameRoleId).cards);
			for (int pai : paiList) {
				int loc = -1;
				if (pai != 0xE && pai != 0xF) {
					for (int i = 1; i <= 4; i++) {
						loc = cards.indexOf(pai + 16 * i);
						if (loc > -1) {
							break;
						}
					}
				} else {
					loc = cards.indexOf(pai);
				}
				/*
				 * if(loc == -1){ this.gameRoleIdSendCard(new
				 * ArrayList<Integer>(1), gameId, gameRoleId); return; }
				 */
				putOut.add(cards.remove(loc));
			}
		}
		/* this.gameRoleIdSendCard(putOut, gameId, gameRoleId); */
		// List<Integer> t = new ArrayList(1);
		Game game = GameCache.getGameMap().get(gameId);

		CardSort cardSort = new CardSort();
		if (game.getLastCardList() != null && game.getRoleIdMap().get(gameRoleId).roleId != 0
				&& game.getRoleIdMap().get(gameRoleId).auto < 2) {
			putOut = new ArrayList<Integer>(1);
			game.getRoleIdMap().get(gameRoleId).auto++;
		} else if (game.getRoleIdMap().get(gameRoleId).roleId != 0 && game.getRoleIdMap().get(gameRoleId).auto < 2) {
			game.getRoleIdMap().get(gameRoleId).auto++;
		}
		if (game.getRoleIdMap().get(gameRoleId).auto == 2) {
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				SessionUtils.sc(
						info.roleId,
						SC.newBuilder()
								.setSCFightAuto(
										SCFightAuto
												.newBuilder()
												.setSeated(
														game.getRoleIdList().indexOf(
																game.getRoleIdMap().get(gameRoleId).gameRoleId))
												.setAuto(true)).build());
				this.notifyObservers(
						FightConstant.FIGHT_RECORD,
						info,
						SC.newBuilder()
								.setSCFightAuto(
										SCFightAuto
												.newBuilder()
												.setSeated(
														game.getRoleIdList().indexOf(
																game.getRoleIdMap().get(gameRoleId).gameRoleId))
												.setAuto(true)).build());
			}
		}
		CardTools.fillCardSort(cardSort, putOut);

		// 如果没有先前的牌型，则为主动出牌,直接判断牌型<br>否则通过使用上一轮牌型判断
		CardList sendCardList = game.getLastCardList() == null ? initiativeSend(cardSort, putOut) : passiveSend(game
				.getLastCardList().getClass(), cardSort, putOut);
		if (sendCardList == null) {
			game.setPassCount(game.getPassCount() + 1);
		}
		this.sendCard(putOut, gameId, gameRoleId, sendCardList);
		boolean allowGuo = game.getPassCount() < (game.getMaxRoleCount() - 1);

		SCFightPutOutPai.Builder SCPutOutBuilder = SCFightPutOutPai.newBuilder().addAllPutOutPai(putOut)
				.setSeated(game.getCurrentRoleIdIndex()).setTimes((int) Math.pow(2, game.getMultiple()))
				.setType(sendCardList == null ? "guo" : sendCardList.getClass().getSimpleName());
		for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
			SCPutOutBuilder.addPaiNum(PaiNum.newBuilder().setSeated(game.getRoleIdList().indexOf(tInfo.gameRoleId))
					.setNum(tInfo.cards.size()));

		}
		SCFightPutOutPai sCFightPutOutPai = SCPutOutBuilder.build();
		for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
			SessionUtils.sc(tInfo.roleId, SC.newBuilder().setSCFightPutOutPai(sCFightPutOutPai).build());
			this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo,
					SC.newBuilder().setSCFightPutOutPai(sCFightPutOutPai).build());
			if (gameRoleId.equals(game.getLandlordGameRoleId()) && game.isMingPaiState()) {
				RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
				SessionUtils.sc(
						tInfo.roleId,
						SC.newBuilder()
								.setSCFightMingPai(
										SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
												.setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId))).build());
				this.notifyObservers(
						FightConstant.FIGHT_RECORD,
						tInfo,
						SC.newBuilder()
								.setSCFightMingPai(
										SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
												.setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId))).build());
			}
			if (game.getRecords().get(game.getRecords().size() - 1).size() == 3) {
				SessionUtils.sc(tInfo.roleId,
						SC.newBuilder().setSCFightLastRoundReady(SCFightLastRoundReady.newBuilder()).build());
				this.notifyObservers(FightConstant.FIGHT_RECORD, tInfo,
						SC.newBuilder().setSCFightLastRoundReady(SCFightLastRoundReady.newBuilder()).build());
			}
		}
		notifyObservers(FightConstant.SEND_CARD, gameId, gameRoleId, sendCardList);

		// 检查游戏是否结束
		if (!this.checkGameOver(gameId)) {
			// 如果没有结束，则请求下一个人出牌
			// 将索引换到下一个人
			getNextIndex(gameId);

			game.setSendCardCount(game.getSendCardCount() + 1);
			game.setSendCardTime(TimeUtils.getNowTime());
			for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
				SessionUtils
						.sc(tInfo.roleId,
								SC.newBuilder()
										.setSCFightPutOut(
												SCFightPutOut
														.newBuilder()
														.setCountdown(
																game.getRoleIdMap().get(
																		game.getRoleIdList().get(
																				game.getCurrentRoleIdIndex())).auto < 2 ? FightConstant.COUNTDOWN : 1)
														.setSeated(game.getCurrentRoleIdIndex()).setAllowGuo(allowGuo))
										.build());
				this.notifyObservers(
						FightConstant.FIGHT_RECORD,
						tInfo,
						SC.newBuilder()
								.setSCFightPutOut(
										SCFightPutOut
												.newBuilder()
												.setCountdown(
														game.getRoleIdMap().get(
																game.getRoleIdList().get(game.getCurrentRoleIdIndex())).auto < 2 ? FightConstant.COUNTDOWN : 1)
												.setSeated(game.getCurrentRoleIdIndex()).setAllowGuo(allowGuo)).build());
			}

			notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
		}
	}

	/**
	 * 实现自动出牌
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月2日
	 */
	private List<Integer> getAutoPaiList(int gameId) {
		List<List<Integer>> priorityList = getRecommandCardList(gameId);
		if (priorityList == null || priorityList.size() == 0) {
			return null;
		}
		return priorityList.get(0);
	}

	private int getNextIndex(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int index = game.getCurrentRoleIdIndex();
		game.setCurrentRoleIdIndex((index + 1) >= game.getRoleIdList().size() ? 0 : index + 1);
		return game.getCurrentRoleIdIndex();
	}

	@Override
	public void getBestCardList(Role role) {
		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);

		// 获得上一次的出牌
		CardList lastCardList = game.getLastCardList();

		// 获得该玩家信息
		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		// 获得玩家卡牌
		CardSort cardSort = new CardSort();
		CardTools.fillCardSort(cardSort, roleGameInfo.cards);

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
	public boolean checkGameOver(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.cards.size() == 0) {
				// 该玩家是赢家
				gameOver(game, info.gameRoleId);
				return true;
			}
		}

		return false;
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

	/**
	 * 获得当前玩家的信息
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月2日
	 */
	private RoleGameInfo getCurrentRoleGameInfo(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int index = game.getCurrentRoleIdIndex();
		String gameRoleId = game.getRoleIdList().get(index);
		return game.getRoleIdMap().get(gameRoleId);
	}

	public static void main(String[] args) {
		// test_dispatchCard();
		// test_linkedMap();
		test_call_landlord();
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

	private static void test_map() {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(1, 1);
		map.put(2, 2);
		map.put(3, 3);
		Set<Integer> set = map.keySet();
		Set<Integer> set2 = map.keySet();
		map.remove(1);
		System.out.println(set);
	}

	private static void test_call_landlord() {
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
			game.getRoleIdList().add(info.gameRoleId);
		}

		impl.gameInit(1);
		impl.dispatchCard(1);

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			System.out.print(info.gameRoleId + "=");
			for (int i : info.cards)
				System.out.print(CardTools.toNum(i) + ",");
			System.out.println();
		}
		for (int card : game.getLandlordCards())
			System.out.print(CardTools.toNum(card) + ",");

		game.setCurrentRoleIdIndex(0);
		System.out.println();

		impl.callLandlord(1, 0 + "", 0);
		System.out.println("mul=" + game.getMultiple() + " score=" + game.getCallLandlordScore());
		impl.callLandlord(1, 1 + "", 0);
		System.out.println("mul=" + game.getMultiple() + " score=" + game.getCallLandlordScore());
		impl.callLandlord(1, 2 + "", 0);
		System.out.println("mul=" + game.getMultiple() + " score=" + game.getCallLandlordScore());
		game.setCurrentRoleIdIndex(0);
		impl.callLandlord(1, 0 + "", 1);
		System.out.println("mul=" + game.getMultiple() + " score=" + game.getCallLandlordScore());
		impl.callLandlord(1, 1 + "", 3);
		System.out.println("mul=" + game.getMultiple() + " score=" + game.getCallLandlordScore());
		impl.callLandlord(1, 2 + "", 2);
		System.out.println("mul=" + game.getMultiple() + " score=" + game.getCallLandlordScore());

	}

	private static void test_send_card_clock() {

	}

}
