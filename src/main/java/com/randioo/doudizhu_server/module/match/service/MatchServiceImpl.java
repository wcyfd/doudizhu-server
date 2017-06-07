package com.randioo.doudizhu_server.module.match.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.dao.RoleDao;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.entity.po.RoleMatchRule;
import com.randioo.doudizhu_server.module.login.service.LoginService;
import com.randioo.doudizhu_server.module.money.service.MoneyExchangeService;
import com.randioo.doudizhu_server.protocol.Entity.GameConfig;
import com.randioo.doudizhu_server.protocol.Entity.GameRoleData;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Match.MatchCreateGameResponse;
import com.randioo.doudizhu_server.protocol.Match.MatchJoinGameResponse;
import com.randioo.doudizhu_server.protocol.Match.SCMatchJoinGame;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.doudizhu_server.util.Tool;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.module.match.MatchHandler;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.module.match.MatchRule;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("matchService")
public class MatchServiceImpl extends ObserveBaseService implements MatchService {

	@Autowired
	private IdClassCreator idClassCreator;

	@Autowired
	private LoginService loginService;

	@Autowired
	private MatchModelService matchModelService;

	@Autowired
	private MoneyExchangeService moneyExchangeService;

	@Autowired
	private RoleDao roleDao;

	@Override
	public void initService() {
		matchModelService.setMatchHandler(new MatchHandler() {

			@Override
			public void outOfTime(MatchRule matchRule) {
				RoleMatchRule roleMatchRule = (RoleMatchRule) matchRule;
				int roleId = roleMatchRule.getRoleId();
				// if (roleMatchRule.isAi()) {
				// Game game = createGame(roleId, gameConfig);
				// addAccountRole(game, roleId);
				// int maxCount = game.getMaxRoleCount();
				// for (int i = game.getRoleIdMap().size(); i < maxCount; i++) {
				// addAIRole(game);
				// }
				// }

				System.out.println(TimeUtils.getNowTime() + " out of Time");
			}

			@Override
			public void matchSuccess(Map<String, MatchRule> matchMap) {
				List<RoleMatchRule> list = new ArrayList<>(matchMap.size());
				for (MatchRule matchRule : matchMap.values())
					list.add((RoleMatchRule) matchRule);

				Collections.sort(list);
				GameConfig config = GameConfig.newBuilder().setDi(3).setMingpai(true).setMoguai(true).setRound(1)
						.build();
				Game game = createGame(list.get(0).getRoleId(), config);
				game.setGameType(GameType.GAME_TYPE_MATCH);

				for (MatchRule matchRule : matchMap.values()) {
					RoleMatchRule rule = (RoleMatchRule) matchRule;

					addAccountRole(game, rule.getRoleId());
				}

			}

			@Override
			public boolean checkMatchRule(MatchRule rule1, MatchRule rule2) {
				RoleMatchRule roleRule1 = (RoleMatchRule) rule1;
				RoleMatchRule roleRule2 = (RoleMatchRule) rule2;

				return roleRule1.getMaxCount() == roleRule2.getMaxCount();
			}

			@Override
			public boolean checkArriveMaxCount(MatchRule rule, Map<String, MatchRule> matchRuleMap) {
				RoleMatchRule roleRule = (RoleMatchRule) rule;

				return matchRuleMap.size() == roleRule.getMaxCount();
			}
		});

		matchModelService.initService();
	}

	@Override
	public GeneratedMessage createRoom(Role role, GameConfig gameConfig) {
		if (!checkConfig(gameConfig)) {
			return SC.newBuilder()
					.setMatchCreateGameResponse(
							MatchCreateGameResponse.newBuilder().setErrorCode(ErrorCode.CREATE_FAILED.getNumber()))
					.build();
		}
		if (!moneyExchangeService.exchangeMoney(role, gameConfig.getRound() / 6 * 20, true)) {
			if (role.getRandiooMoney() - gameConfig.getRound() / 6 * 20 < 0) {
				return SC.newBuilder()
						.setMatchCreateGameResponse(
								MatchCreateGameResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber()))
						.build();
			}
		}
		Game game = this.createGame(role.getRoleId(), gameConfig);
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(this.getGameRoleId(game.getGameId(), role.getRoleId()));

		GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game.getGameId());
		return SC.newBuilder()
				.setMatchCreateGameResponse(MatchCreateGameResponse.newBuilder().setId(game.getLockString())
						.setGameRoleData(myGameRoleData).setMoguai(game.getGameConfig().getMoguai()))
				.build();
	}

	/**
	 * 创建游戏
	 * 
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月26日
	 */
	private Game createGame(int roleId, GameConfig gameConfig) {
		Game game = new Game();
		int gameId = idClassCreator.getId(Game.class);
		game.setGameId(gameId);
		game.setGameType(GameType.GAME_TYPE_FRIEND);
		game.setGameState(GameState.GAME_STATE_PREPARE);

		game.setMasterRoleId(roleId);
		game.setLockString(this.getLockString());
		game.setMaxRoleCount(3);

		this.addAccountRole(game, roleId);

		game.setGameConfig(gameConfig);
		game.setRound(gameConfig.getRound());

		GameCache.getGameMap().put(gameId, game);
		GameCache.getGameLockStringMap().put(game.getLockString(), gameId);

		return game;
	}

	/**
	 * 加入玩家
	 * 
	 * @param game
	 * @param roleId
	 * @author wcy 2017年5月26日
	 */
	private void addAccountRole(Game game, int roleId) {
		String gameRoleId = getGameRoleId(game.getGameId(), roleId);

		addRole(game, roleId, gameRoleId);
	}

	/**
	 * 加入ai
	 * 
	 * @param game
	 * @author wcy 2017年5月26日
	 */
	private String addAIRole(Game game) {
		String gameRoleId = this.getAIGameRoleId(game.getGameId());

		addRole(game, 0, gameRoleId);
		// 机器人自动准备完毕
		game.getRoleIdMap().get(gameRoleId).ready = true;
		return gameRoleId;
	}

	private void addRole(Game game, int roleId, String gameRoleId) {
		RoleGameInfo roleGameInfo = this.createRoleGameInfo(roleId, gameRoleId);
		// roleGameInfo.seatIndex = game.getRoleIdMap().size();
		if (roleId != 0) {
			Role role = (Role) RoleCache.getRoleById(roleId);
			role.setGameId(game.getGameId());
		}
		game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);
		game.getRoleIdList().add(gameRoleId);
	}

	/**
	 * 创建用户在游戏中的数据结构
	 * 
	 * @param roleId
	 * @param gameId
	 * @return
	 * @author wcy 2017年5月25日
	 */
	private RoleGameInfo createRoleGameInfo(int roleId, String gameRoleId) {
		RoleGameInfo roleGameInfo = new RoleGameInfo();
		roleGameInfo.roleId = roleId;
		roleGameInfo.gameRoleId = gameRoleId;

		return roleGameInfo;
	}
	@Override
	public Role getRoleFromRoleGameInfo(RoleGameInfo info) {
		int roleId = info.roleId;
		if(roleId == 0){
			Role role = new Role();

			role.setName("ROBOT"+info.roleId);
			return role;
		}
		return (Role) RoleCache.getRoleById(roleId);
	}

	@Override
	public GeneratedMessage joinGame(Role role, String lockString) {
		Integer gameId = GameCache.getGameLockStringMap().get(lockString);
		System.out.println("gameid:" + gameId);
		if (gameId == null) {
			return SC.newBuilder()
					.setMatchJoinGameResponse(
							MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber()))
					.build();
		}

		Game game = GameCache.getGameMap().get(gameId);
		System.out.println("game:" + game);
		if (game == null) {
			return SC.newBuilder()
					.setMatchJoinGameResponse(
							MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber()))
					.build();
		}
		String targetLock = game.getLockString();
		// 如果锁相同则可以进
		if (!targetLock.equals(lockString)) {
			return SC.newBuilder()
					.setMatchJoinGameResponse(
							MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.MATCH_ERROR_LOCK.getNumber()))
					.build();
		}

		if (game.getRoleIdMap().size() >= game.getMaxRoleCount()) {
			return SC.newBuilder()
					.setMatchJoinGameResponse(
							MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.MATCH_MAX_ROLE_COUNT.getNumber()))
					.build();
		}

		this.addAccountRole(game, role.getRoleId());

		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(this.getGameRoleId(game.getGameId(), role.getRoleId()));

		GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game.getGameId());
		SC scJoinGame = SC.newBuilder().setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(myGameRoleData))
				.build();
		// 通知其他人加入房间
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (role.getRoleId() == info.roleId)
				continue;
			SessionUtils.sc(info.roleId, scJoinGame);
		}

		List<GameRoleData> gameRoleDataList = new ArrayList<>(game.getRoleIdMap().size());
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			GameRoleData gameRoleData = this.parseGameRoleData(info, game.getGameId());
			gameRoleDataList.add(gameRoleData);
		}
		return SC.newBuilder().setMatchJoinGameResponse(MatchJoinGameResponse.newBuilder()
				.addAllGameRoleData(gameRoleDataList).setSeated(myGameRoleData.getSeated()).setId(lockString).setMoguai(game.getGameConfig().getMoguai()))
				.build();
	}

	@Override
	public GeneratedMessage match(Role role) {
		RoleMatchRule matchRule = new RoleMatchRule();
		matchRule.setId(idClassCreator.getId(RoleMatchRule.class) + "_" + role.getRoleId());
		matchRule.setWaitTime(60);
		matchRule.setAi(false);
		matchRule.setMatchTime(TimeUtils.getNowTime());
		matchModelService.matchRole(matchRule);
		return null;
	}

	@Override
	public void matchAI(Role role) {
		int roleId = role.getRoleId();
		GameConfig config = GameConfig.newBuilder().setDi(1).setMingpai(true).setMoguai(true).setRound(1).build();
		Game game = createGame(roleId, config);
		RoleGameInfo tRoleGameInfo = game.getRoleIdMap().get(this.getGameRoleId(game.getGameId(), role.getRoleId()));

		GameRoleData myGameRoleData = this.parseGameRoleData(tRoleGameInfo, game.getGameId());
		SessionUtils
				.sc(role.getRoleId(),
						SC.newBuilder()
								.setMatchCreateGameResponse(MatchCreateGameResponse.newBuilder()
										.setId(game.getLockString()).setGameRoleData(myGameRoleData)
										.setMoguai(game.getGameConfig().getMoguai()))
								.build());
		int maxCount = game.getMaxRoleCount();
		for (int i = game.getRoleIdMap().size(); i < maxCount; i++) {
			String gameRoleId = addAIRole(game);

			RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
			System.out.println(info);
			int index = game.getRoleIdList().indexOf(gameRoleId);
			GameRoleData AIGameRoleData = GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready)
					.setSeated(index).setName("ROBOT" + info.gameRoleId).build();

			SC scJoinGame = SC.newBuilder()
					.setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(AIGameRoleData)).build();
			SessionUtils.sc(role.getRoleId(), scJoinGame);
		}

	}

	private GameRoleData parseGameRoleData(RoleGameInfo info, int gameId) {
		Role role = loginService.getRoleById(info.roleId);
		String name = role != null ? role.getName() : "";

		Game game = GameCache.getGameMap().get(gameId);
		int index = game.getRoleIdList().indexOf(info.gameRoleId);
		return GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready).setSeated(index)
				.setName(name).setHeadImgUrl(role.getHeadImgUrl()).setMoney(role.getMoney()).build();
	}

	/**
	 * 游戏内使用的玩家id
	 * 
	 * @param gameId
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月24日
	 */
	@Override
	public String getGameRoleId(int gameId, int roleId) {
		return gameId + "_" + roleId;
	}

	/**
	 * 
	 * @param gameId
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月24日
	 */
	private String getAIGameRoleId(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int aiCount = 0;
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			if (roleGameInfo.roleId == 0) {
				aiCount++;
			}
		}
		return gameId + "_0_" + aiCount;
	}

	private String getLockString() {
		return /* "1980" */"111111";
	}

	public boolean checkConfig(GameConfig gameConfig) {
		int[] di = { 1, 2, 3, 5 };
		int[] round = { 6, 12, 18, 24 };
		if (Tool.indexOf(di, gameConfig.getDi()) == -1 || Tool.indexOf(round, gameConfig.getRound()) == -1) {
			return false;
		}
		return true;
	}

}
