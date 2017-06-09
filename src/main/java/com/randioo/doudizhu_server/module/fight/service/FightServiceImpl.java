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
import com.randioo.doudizhu_server.entity.po.CallLandLordTimeEvent;
import com.randioo.doudizhu_server.entity.po.CardRecord;
import com.randioo.doudizhu_server.entity.po.CardSort;
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
import com.randioo.doudizhu_server.module.score.service.ScoreService;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.PaiNum;
import com.randioo.doudizhu_server.protocol.Entity.Record;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Fight.FightAgreeExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightCallLandLordResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightExitGameResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightGetlastRoundResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightMingPaiResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightReadyResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightRecommandResponse;
import com.randioo.doudizhu_server.protocol.Fight.FightRecommandResponse.RecommandPai;
import com.randioo.doudizhu_server.protocol.Fight.FightSendCardResponse;
import com.randioo.doudizhu_server.protocol.Fight.SCAgreeExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightCallLandLord;
import com.randioo.doudizhu_server.protocol.Fight.SCFightExitGame;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameDismiss;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameOver;
import com.randioo.doudizhu_server.protocol.Fight.SCFightGameOver.GameOverData;
import com.randioo.doudizhu_server.protocol.Fight.SCFightLandLord;
import com.randioo.doudizhu_server.protocol.Fight.SCFightMingPai;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPutOut;
import com.randioo.doudizhu_server.protocol.Fight.SCFightPutOutPai;
import com.randioo.doudizhu_server.protocol.Fight.SCFightReady;
import com.randioo.doudizhu_server.protocol.Fight.SCFightRoundOver;
import com.randioo.doudizhu_server.protocol.Fight.SCFightRoundOver.Score;
import com.randioo.doudizhu_server.protocol.Fight.SCFightStart;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.CardTools;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.ReflectUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

	@Autowired
	private MatchService matchService;
	
	@Autowired
	private ScoreService scoreService;

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
		//recommendClasses.add(A3N1.class);
		//recommendClasses.add(A3N2.class);
		recommendClasses.add(A3B3.class);
		recommendClasses.add(ABCDE.class);
		recommendClasses.add(A2B2C2.class);
		//recommendClasses.add(A3B3CD.class);
		//recommendClasses.add(A3B3C2D2.class);
		//recommendClasses.add(A4BC.class);
		//recommendClasses.add(A4B2C2.class);
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
		if (msg.equals(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD)) {
			Game game = GameCache.getGameMap().get(args[0]);
			String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
			RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
			System.out.println("@@@" + (game.getRoleIdList().indexOf(info.gameRoleId) == game.getCurrentRoleIdIndex()));
			if (info.roleId == 0 && game.getRoleIdList().indexOf(info.gameRoleId) == game.getCurrentRoleIdIndex()) {
				CallLandLordTimeEvent callLandLordTimeEvent = new CallLandLordTimeEvent() {

					@Override
					public void update(TimeEvent timeEvent) {
						timeUp((CallLandLordTimeEvent) timeEvent);
					}
				};

				callLandLordTimeEvent.setGameRoleId(gameRoleId);
				callLandLordTimeEvent.setEndTime(TimeUtils.getNowTime() + 3/*FightConstant.SEND_CARD_WAIT_TIME*/);
				callLandLordTimeEvent.setGameId(game.getGameId());

				eventScheduler.addEvent(callLandLordTimeEvent);
				int t = RandomUtils.getRandomNum(3);
				
				System.out.println(info.roleId + "不叫");
			}
		}
		if (msg.equals(FightConstant.APPLY_LEAVE)) {
			RoleGameInfo info = (RoleGameInfo) args[1];
			Game game = GameCache.getGameMap().get(args[0]);			
			if (info.roleId == 0) {
				agreeExit((int) args[0], info.gameRoleId, true);
			}
		}
		if (msg.equals(FightConstant.NEXT_GAME_ROLE_SEND_CARD)) {
			int gameId = (int) args[0];
			if(this.getCurrentRoleGameInfo(gameId).roleId == 0){
				this.checkAutoAI(gameId);
			}			
		}
		if(msg.equals("MINGPAI")){
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
				}
			}
			
		}
	}
	
	@Override
	public void initService() {
		this.addObserver(this);
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
						SCFightReady.newBuilder().setSeated(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)))
				.build();
		for (RoleGameInfo info : game.getRoleIdMap().values())
			SessionUtils.sc(info.roleId, scFightReady);

		// 检查是否全部都准备完毕,全部准备完毕开始游戏
		if (this.checkAllReady(role.getGameId())) {
			game.getRecords().add(new ArrayList<List<CardRecord>>());
			game.setGameState(GameState.GAME_START_START);
			// 游戏初始化
			this.gameInit(game.getGameId());
			
			game.setRound(game.getRound() - 1);
			SCFightStart.Builder FSBuilder = SCFightStart.newBuilder();
			for (int i = 0; i < game.getMaxRoleCount(); i++) {
				FSBuilder.addPaiNum(PaiNum.newBuilder().setSeated(i)
						.setNum(game.getRoleIdMap().get(game.getRoleIdList().get(i)).cards.size()));

			}
			FSBuilder.setTimes(game.getMultiple());
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				System.out.println("~~~" + info.roleId + game.getMultiple());
				SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightStart(FSBuilder.clone().addAllPai(info.cards))
						.build());
				SessionUtils.sc(
						info.roleId,
						SC.newBuilder()
								.setSCFightCallLandLord(
										SCFightCallLandLord.newBuilder().setCurrentFen(0)
												.setSeated(game.getCurrentRoleIdIndex())
												.setCountdown(FightConstant.COUNTDOWN)
												.setFen(-1)).build());
			}
			this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
		}
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
		game.getRecords().get(game.getRecords().size()-1).add(new ArrayList<CardRecord>());
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
			SessionUtils.sc(role.getRoleId(), 
					SC.newBuilder().setFightMingPaiResponse(
							FightMingPaiResponse.newBuilder()
												.setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					  .build());
		}
		RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
		if (game.getRoleIdMap().get(game.getLandlordGameRoleId()).roleId != role.getRoleId()) {
			SessionUtils.sc(role.getRoleId(), 
					SC.newBuilder().setFightMingPaiResponse(
							FightMingPaiResponse.newBuilder()
												.setErrorCode(ErrorCode.NOT_LANDLORD.getNumber()))
					  .build());
		}
		if (!game.getGameConfig().getMingpai()) {
			SessionUtils.sc(role.getRoleId(), 
					SC.newBuilder().setFightMingPaiResponse(
							FightMingPaiResponse.newBuilder()
												.setErrorCode(ErrorCode.MINGPAI_FORBIDDEN.getNumber()))
					  .build());
		}
		game.setMultiple(game.getMultiple() + 1);
		game.setMingPaiState(true);
		SC sc = SC
				.newBuilder()
				.setSCFightMingPai(
						SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
								.setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId))).build();
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			SessionUtils.sc(info.roleId, sc);
		}
		SessionUtils.sc(role.getRoleId(), 
				SC.newBuilder().setFightMingPaiResponse(FightMingPaiResponse.newBuilder()).build());

	}
	@Override
	public GeneratedMessage getLastRecord (int gameId){
		Game game = GameCache.getGameMap().get(gameId);
		if(game == null){
			return SC.newBuilder().setFightGetlastRoundResponse(FightGetlastRoundResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())).build(); 
		}
		List<List<CardRecord>>  records = game.getRecords().get(game.getRecords().size()-1);
		if(records.size() < 2){
			return SC.newBuilder().setFightGetlastRoundResponse(FightGetlastRoundResponse.newBuilder().setErrorCode(ErrorCode.FIRST_ROUND.getNumber())).build(); 
		}
		FightGetlastRoundResponse.Builder builder = FightGetlastRoundResponse.newBuilder();
		List<CardRecord> record = records.get(records.size() - 2);
		for(CardRecord temp : record){
			builder.addRecords(Record.newBuilder().setSeated(game.getRoleIdList().indexOf(temp.gameRoleId))
												  .addAllPai(temp.cards));
		}
		return SC.newBuilder().setFightGetlastRoundResponse(builder).build();
		
	}
	@Override
	public GeneratedMessage exitGame(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			return SC
					.newBuilder()
					.setFightExitGameResponse(
							FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build();
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

		GameState gameState = game.getGameState();
		// 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
		if (gameState == GameState.GAME_STATE_PREPARE && game.getRound() == game.getGameConfig().getRound()) {
			// 若是房主，则直接解散
			if (game.getMasterRoleId() == role.getRoleId()) {
				game.setGameState(GameState.GAME_START_END);

				SC scDismiss = SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
				for (RoleGameInfo info : game.getRoleIdMap().values())
					SessionUtils.sc(info.roleId, scDismiss);
				GameCache.getGameLockStringMap().remove(game.getLockString());
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
				return SC
						.newBuilder()
						.setFightExitGameResponse(
								FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_EXITING.getNumber()))
						.build();
			}
			SC scApplyExit = SC
					.newBuilder()
					.setSCFightApplyExitGame(
							SCFightApplyExitGame.newBuilder().setName(role.getName())
									.setCountDown(FightConstant.COUNTDOWN)).build();
			setOnlineCount(game.getGameId());
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				if (info.roleId != role.getRoleId()) {
					SessionUtils.sc(info.roleId, scApplyExit);
					this.notifyObservers(FightConstant.APPLY_LEAVE, game.getGameId(), info);
				}				
			}
			agreeExit(role, true);
		}

		return SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build();
	}
	private void setOnlineCount(int gameId){
		Game game = GameCache.getGameMap().get(gameId);
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (SessionCache.getSessionById(info.roleId) == null
					|| SessionCache.getSessionById(info.roleId).isConnected()) {
				game.setOnlineRoleCount(game.getOnlineRoleCount() + 1);
			}
			info.agreeLeave = null;			
		}
	}

	@Override
	public GeneratedMessage agreeExit(Role role, boolean agree) {
		System.out.println(role.getRoleId() + "" + agree);
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			return SC
					.newBuilder()
					.setFightAgreeExitGameResponse(
							FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build();
		} else {
			String roleInfoStr = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
			return agreeExit(game.getGameId(), roleInfoStr, agree);
		}
	}

	public GeneratedMessage agreeExit(int gameId, String exitGameRoleId, boolean agree) {
		Game game = GameCache.getGameMap().get(gameId);
		RoleGameInfo roleInfo = game.getRoleIdMap().get(exitGameRoleId);
		synchronized (game) {
			roleInfo.agreeLeave = agree;
			if (game.getOnlineRoleCount() == 0) {
				return SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build();
			}
			int flag = 0;
			RoleGameInfo rejectRoleGameInfo = null;
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				if (info.agreeLeave != null && info.agreeLeave == false) {
					flag = -1;
					game.setOnlineRoleCount(0);
					rejectRoleGameInfo = info;
					break;
				}
				if (info.agreeLeave != null && info.agreeLeave) {
					flag += 1;
				}
			}
			if (flag == -1) {
				for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
					SessionUtils.sc(roleGameInfo.roleId,
							SC.newBuilder()
									.setSCAgreeExitGame(SCAgreeExitGame.newBuilder()
											.setName(rejectRoleGameInfo.roleId == 0
													? "ROBOT" + rejectRoleGameInfo.gameRoleId
													: RoleCache.getRoleById(rejectRoleGameInfo.roleId).getName()))
									.build());
				}
				return SC.newBuilder().setFightAgreeExitGameResponse(
						FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.APPLY_REJECT.getNumber()))
						.build();
			} else if (flag == game.getOnlineRoleCount()) {
				game.setGameState(GameState.GAME_START_END);
				SC scDismiss = SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
				for (RoleGameInfo info : game.getRoleIdMap().values())
					SessionUtils.sc(info.roleId, scDismiss);

				// 将游戏从缓存池中移除
				GameCache.getGameLockStringMap().remove(game.getLockString());
				GameCache.getGameMap().remove(game.getGameId());
			}
		}
		
		return SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build();
	}

	@Override
	public void dispatchCard(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		for (RoleGameInfo info : game.getRoleIdMap().values())
			info.cards.clear();

		int maxCount = game.getMaxRoleCount();
		int needCard = 1;
		int totalCardCount = (FightConstant.CARDS.length - maxCount) / maxCount;
		int landlordCardBoxIndex = RandomUtils.getRandomNum(needCard);
		int landlordCardIndex = RandomUtils.getRandomNum(FightConstant.CARDS.length);
		/*int [][]card = {
				{0x11, 0x21, 0x31, 0x41, 0x12, 0x22, 0x32, 0x42, 0x13, 0x23, 0x33, 0x43, 0x14, 0x24, 0x34, 0x44, 0x1D},
				{0x15, 0x25, 0x35, 0x45, 0x16, 0x26, 0x36, 0x46, 0x17, 0x27, 0x37, 0x47, 0x18, 0x28, 0x38, 0x48, 0x3C},
				{0x19, 0x29, 0x39, 0x49, 0x1A, 0x2A, 0x3A, 0x4A, 0x1B, 0x2B, 0x3B, 0x4B, 0x1C, 0x2C, 0x3C, 0x0E, 0x0F}
		};
		int []landlord = {0x2D, 0x3D, 0x4D};*/
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
						if((value == CardTools.C_KING || value == CardTools.C_QUEUE)&&(game.getGameConfig().getMoguai())){
							game.setMoGuai(true);
							game.setMultiple(game.getMultiple()
									+ 1);
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
		/*for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()){
			for(int t : card[game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)])
				roleGameInfo.cards.add(t);
		}
		game.setCurrentRoleIdIndex(0);
		for(int t : landlord)
			game.getLandlordCards().add(t);*/
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
		if(role.getRoleId() != this.getCurrentRoleGameInfo(gameId).roleId){
			return SC.newBuilder().setFightRecommandResponse(
									FightRecommandResponse.newBuilder()
									  	  				  .setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
					 .build();
									
		}
		List<List<Integer>> list = this.getRecommandCardList(gameId);
		FightRecommandResponse.Builder builder = FightRecommandResponse.newBuilder();
		for(List<Integer> temp : list){
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
		//先检查牌型，如果手牌成牌型，并且能出
		if(lastCardList == null){
			for (CardList cardList : GameCache.getCardLists().values()) {
				CardList sendCardList = checkCardList(cardList, cardSort, roleGameInfo.cards);
				if (sendCardList != null && (lastCardList == null || sendCardListBiggerThanLastCardList(sendCardList, lastCardList))){
					List<Integer> tList = new ArrayList<Integer>();
					for(int t : roleGameInfo.cards){
						tList.add(CardTools.toNum(t));
					}
					recommandList.add(tList);
					System.out.println(tList);
					return recommandList;
				}
			}
		}
		// 手牌不成牌型		
		for (CardList cardList :  GameCache.getRecommendCardLists().values()) {
			cardList.recommand(recommandList, cardSort, lastCardList, roleGameInfo.cards);
		}
		return recommandList;

	}
	
	private void callLandlord(int gameId, String gameRoleId, int callScore) {
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
				for (RoleGameInfo info : game.getRoleIdMap().values()) {
					if (!info.gameRoleId.equals(game.getLandlordGameRoleId())) {
						SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightLandLord(scFightLandLord).build());
					} else {
						SessionUtils.sc(
								info.roleId,
								SC.newBuilder()
										.setSCFightLandLord(
												scFightLandLord.toBuilder().addAllPai(
														game.getRoleIdMap().get(info.gameRoleId).cards)).build());
						
						this.notifyObservers("MINGPAI",game.getGameId(),info.roleId);
					}
					SessionUtils.sc(
							info.roleId,
							SC.newBuilder()
									.setSCFightPutOut(
											SCFightPutOut.newBuilder().setCountdown(FightConstant.COUNTDOWN)
													.setSeated(game.getCurrentRoleIdIndex()).setAllowGuo(false)).build());
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
				FSBuilder.setTimes(game.getMultiple());
				for (RoleGameInfo info : game.getRoleIdMap().values()) {
					System.out.println("~~~" + info.roleId + game.getMultiple());
					SessionUtils.sc(info.roleId,
							SC.newBuilder().setSCFightStart(FSBuilder.clone().addAllPai(info.cards)).build());
					SessionUtils.sc(
							info.roleId,
							SC.newBuilder()
									.setSCFightCallLandLord(
											SCFightCallLandLord.newBuilder().setCurrentFen(0)
													.setSeated(game.getCurrentRoleIdIndex())
													.setCountdown(FightConstant.COUNTDOWN)
													.setFen(-1)).build());
				}
				this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
				return;
			} else {
				// 看叫分最高的是谁
				System.out.println("地主是:" + game.getLandlordGameRoleId());
				this.giveLandlordCards(gameId);
				game.setCurrentRoleIdIndex(game.getRoleIdList().indexOf(game.getLandlordGameRoleId()));
				SCFightLandLord scFightLandLord = SCFightLandLord.newBuilder().addAllLandLordPai(game.getLandlordCards())
						.setSeated(game.getRoleIdList().indexOf(game.getLandlordGameRoleId())).build();
				for (RoleGameInfo info : game.getRoleIdMap().values()) {
					if (!info.gameRoleId.equals(game.getLandlordGameRoleId())) {
						SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightLandLord(scFightLandLord).build());
					} else {
						SessionUtils.sc(
								info.roleId,
								SC.newBuilder()
										.setSCFightLandLord(
												scFightLandLord.toBuilder().addAllPai(
														game.getRoleIdMap().get(info.gameRoleId).cards)).build());
						this.notifyObservers("MINGPAI",game.getGameId(),info.roleId);
					}
					SessionUtils.sc(
							info.roleId,
							SC.newBuilder()
									.setSCFightPutOut(
											SCFightPutOut.newBuilder().setCountdown(FightConstant.COUNTDOWN)
													.setSeated(game.getRoleIdList().indexOf(game.getLandlordGameRoleId())).setAllowGuo(false)).build());
				}
				notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
			}
		} else {
			// 如果人数没有到,则通知下一个人
			System.out.println("22222---" + callCount);
			int nextIndex = this.getNextIndex(gameId);
			String nextRoleGameId = game.getRoleIdList().get(nextIndex);
			int roleId = game.getRoleIdMap().get(nextRoleGameId).roleId;
			game.setCallLandlordCount(callCount);
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				SessionUtils.sc(
						info.roleId,
						SC.newBuilder()
								.setSCFightCallLandLord(
										SCFightCallLandLord.newBuilder().setCurrentFen(game.getCallLandlordScore())
												.setSeated(nextIndex).setCountdown(FightConstant.COUNTDOWN)
												.setFen(callScore)).build());
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
		// 自动出牌解除
		roleGameInfo.auto = 0;
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
			if (sendCardList == null){
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
					if (!bigger){
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
		CardRecord record = new CardRecord();
		record.cards = paiList;
		record.gameRoleId = gameRoleId;
		game.getRecords().get(game.getRecords().size()-1).get(game.getRecords().get(game.getRecords().size()-1).size()-1).add(record);
//		game.getCurrentRecord().add();
		// 如果出牌了，则放弃出牌的计数器重置
		if (sendCardList != null){
			game.setPassCount(0);
			if(sendCardList.getClass() == A4.class || sendCardList.getClass() == KQ.class){
				game.setBomb(game.getBomb() + 1);
				game.setMultiple(game.getMultiple() + 1);
			}
			if(game.isLandLordSpring() && !gameRoleId.equals(game.getLandlordGameRoleId())){
				System.out.println("dizhuSpring!~");
				game.setLandLordSpring(false);
			}
			if(game.isFarmerSpring() && game.getPassCount() < (game.getMaxRoleCount() - 1) && gameRoleId.equals(game.getLandlordGameRoleId())){
				game.setFarmerSpring(false);
				System.out.println("farmerSpring!~");//TODO 地主第二次出牌农民不是春天
			}
			game.setLastCardList(sendCardList);
		}else{
			if(game.getPassCount() >= game.getMaxRoleCount() - 1){
				game.setLastCardList(null);
			}
		}		
		boolean allowGuo = game.getPassCount() < (game.getMaxRoleCount() - 1);
		if(!allowGuo){
			System.out.println(game.getRecords().get(game.getRecords().size()-1).get(game.getRecords().get(game.getRecords().size()-1).size()-1));
			game.getRecords().get(game.getRecords().size()-1).add(new ArrayList<CardRecord>());
		}
		// 从手牌中移除该牌
		RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
		for (int pai : paiList) {
			int index = info.cards.indexOf(pai);
			System.out.println(info.cards+"~~~"+pai+"---"+index);
			info.cards.remove(index);
		}
		SessionUtils.sc(roleId, SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());
		/**
		 * 通知已经出牌
		 */
		SCFightPutOutPai.Builder SCPutOutBuilder = SCFightPutOutPai.newBuilder().addAllPutOutPai(paiList)
				.setSeated(game.getCurrentRoleIdIndex())
				.setTimes(game.getMultiple());
		for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {

			SCPutOutBuilder.addPaiNum(PaiNum.newBuilder().setSeated(game.getRoleIdList().indexOf(tInfo.gameRoleId))
					.setNum(tInfo.cards.size()));

		}
		SCFightPutOutPai sCFightPutOutPai = SCPutOutBuilder.build();
		for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
			SessionUtils.sc(
					tInfo.roleId,
					SC.newBuilder()
							.setSCFightPutOutPai(sCFightPutOutPai)
							.build());
			if (gameRoleId.equals(game.getLandlordGameRoleId()) && game.isMingPaiState()) {
				RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
				SessionUtils.sc(
						tInfo.roleId,
						SC.newBuilder()
								.setSCFightMingPai(
										SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
												.setSeated(game.getRoleIdList().indexOf(LandLord.gameRoleId))).build());
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
				SessionUtils.sc(
						tInfo.roleId,
						SC.newBuilder()
								.setSCFightPutOut(
										SCFightPutOut.newBuilder().setCountdown(FightConstant.COUNTDOWN)
												.setSeated(game.getCurrentRoleIdIndex()).setAllowGuo(allowGuo)).build());
			}

			notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
		}
	}
	private void gameOver(int gameId, String gameRoleId){
		Game game = GameCache.getGameMap().get(gameId);
		if(game == null){
			return;
		}
		scoreService.updateScore(gameId, gameRoleId);
		game.setGameState(GameState.GAME_STATE_PREPARE);
		SCFightRoundOver.Builder builder = SCFightRoundOver.newBuilder();
		builder.setOver(game.getRound() == 0);
		builder.setDi(game.getGameConfig().getDi());
		builder.setBomb(game.getBomb());
		builder.setMingpai(game.isMingPaiState());
		builder.setMoguai(game.isMoGuai());
		System.out.println("dizhuWin:"+gameRoleId.equals(game.getLandlordGameRoleId()) +"-dizhuSpring:"+ game.isLandLordSpring()+game.isFarmerSpring());
		builder.setSpring((gameRoleId.equals(game.getLandlordGameRoleId()) && game.isLandLordSpring())||(!gameRoleId.equals(game.getLandlordGameRoleId()) && game.isFarmerSpring()));
		for(RoleGameInfo info : game.getRoleIdMap().values()){			
			Role role = (Role)RoleCache.getRoleMap().get(info.roleId);
			String name = "";
			int money = 0;
			if(role == null){
				name = "ROBOT"+info.gameRoleId;
			}else{
				info.ready = false;
				name = role.getName();
				money = role.getMoney();
			}
			
			
			builder.addScore(Score.newBuilder().setName(name).setScore(info.currentMark).setAllScore(info.allMark).setSeated(game.getRoleIdList().indexOf(info.gameRoleId)).setMoney(money));
		}
		SC sc = SC.newBuilder().setSCFightRoundOver(builder).build();
		for(RoleGameInfo info : game.getRoleIdMap().values()){
			SessionUtils.sc(info.roleId, sc);
		}
		if(game.getRound() == 0){
			//TODO 解散
			SCFightGameOver.Builder SCGameOverBuilder = SCFightGameOver.newBuilder();
			for(RoleGameInfo info : game.getRoleIdMap().values()){
				GameOverData.Builder gameOverBuilder = GameOverData.newBuilder();
				Role role = (Role)RoleCache.getRoleMap().get(info.roleId);
				String name = "";
				if(role == null){
					name = "ROBOT"+info.gameRoleId;
				}else{
					name = role.getName();
				}
				gameOverBuilder.setFarmerNum(info.farmerNum);
				gameOverBuilder.setLandLordNum(info.landLordNum);
				gameOverBuilder.setName(name);
				gameOverBuilder.setScore(info.allMark);
				SCGameOverBuilder.addGameOverData(gameOverBuilder);				
			}
			SC gameOverSC = SC.newBuilder().setSCFightGameOver(SCGameOverBuilder).build();
			for(RoleGameInfo info : game.getRoleIdMap().values()){
				SessionUtils.sc(info.roleId, gameOverSC);
				SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build());
			}
			GameCache.getGameLockStringMap().remove(game.getLockString());
			GameCache.getGameMap().remove(game.getGameId());
		}
		
	}

	private void checkAutoAI(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		// 发送等待消息
		RoleGameInfo info = getCurrentRoleGameInfo(gameId);
		/*if (info.auto >= 2) {
			autoSendCard(gameId, info.gameRoleId);
			return;
		}*/

		SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

			@Override
			public void update(TimeEvent timeEvent) {
				timeUp((SendCardTimeEvent) timeEvent);
			}
		};

		sendCardTimeEvent.setSendCardCount(game.getSendCardCount());
		sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + 2/*FightConstant.SEND_CARD_WAIT_TIME*/);
		sendCardTimeEvent.setGameId(gameId);

		eventScheduler.addEvent(sendCardTimeEvent);
	}

	private void timeUp(SendCardTimeEvent event) {
		int gameId = event.getGameId();
		Game game = GameCache.getGameMap().get(gameId);
		// 如果出牌数已经改变,或者游戏已经结束,则直接返回
		if (game.getSendCardCount() != event.getSendCardCount())
			return;

		String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());

		this.autoSendCard(gameId, gameRoleId);
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		roleGameInfo.auto++;

		System.out.println("time up");
	}
	private void timeUp(CallLandLordTimeEvent event) {
		int gameId = event.getGameId();
		int t = RandomUtils.getRandomNum(4);
		int currentScore = GameCache.getGameMap().get(gameId).getCallLandlordScore();
		callLandlord(gameId, event.getGameRoleId(), t > currentScore ? t : 0);
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
		if(paiList == null){
			this.gameRoleIdSendCard(new ArrayList<Integer>(0), gameId, gameRoleId);
			return;
		}
		List<Integer> cards = new ArrayList<>();
		List<Integer> putOut = new ArrayList<>();
		cards.addAll(GameCache.getGameMap().get(gameId).getRoleIdMap().get(gameRoleId).cards);
		for(int pai : paiList){
			int loc = -1;
			if(pai != 0xE && pai != 0xF){				
				for(int i = 1 ; i <= 4 ; i ++){
					loc = cards.indexOf(pai+16*i);
					if(loc > -1){
						break;
					}
				}
			}else{
				loc = cards.indexOf(pai);
			}
			/*if(loc == -1){
				this.gameRoleIdSendCard(new ArrayList<Integer>(1), gameId, gameRoleId);
				return;
			}*/
			putOut.add(cards.remove(loc));
		}
		//List<Integer> t = new ArrayList(1);
		this.gameRoleIdSendCard(putOut /*t*/, gameId, gameRoleId);

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
		if(priorityList == null || priorityList.size() == 0){
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
				gameOver(gameId, info.gameRoleId);
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
