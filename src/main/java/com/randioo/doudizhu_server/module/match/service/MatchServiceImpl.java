package com.randioo.doudizhu_server.module.match.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.login.service.LoginService;
import com.randioo.doudizhu_server.protocol.Entity.GameRoleData;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.Match.MatchCreateGameResponse;
import com.randioo.doudizhu_server.protocol.Match.MatchJoinGameResponse;
import com.randioo.doudizhu_server.protocol.Match.SCMatchJoinGame;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.utils.game.IdClassCreator;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;

@Service("matchService")
public class MatchServiceImpl extends ObserveBaseService implements MatchService {

	@Autowired
	private IdClassCreator idClassCreator;

	@Autowired
	private LoginService loginService;

	@Override
	public void initService() {

	}

	@Override
	public GeneratedMessage createGame(Role role) {
		Game game = new Game();
		int gameId = idClassCreator.getId(Game.class);
		game.setGameId(gameId);
		game.setGameType(GameType.GAME_TYPE_FRIEND);
		game.setGameState(GameState.GAME_STATE_PREPARE);

		RoleGameInfo roleGameInfo = this.createRoleGameInfo(role.getRoleId(), gameId);
		roleGameInfo.seatIndex = game.getRoleIdMap().size();
		game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);

		game.setMasterRoleId(role.getRoleId());
		game.setLockString(this.getLockString());
		game.setMaxRoleCount(3);

		GameCache.getGameMap().put(gameId, game);
		GameCache.getGameLockStringMap().put(game.getLockString(), gameId);

		return SC.newBuilder().setMatchCreateGameResponse(MatchCreateGameResponse.newBuilder()).build();
	}

	/**
	 * 创建用户在游戏中的数据结构
	 * 
	 * @param roleId
	 * @param gameId
	 * @return
	 * @author wcy 2017年5月25日
	 */
	private RoleGameInfo createRoleGameInfo(int roleId, int gameId) {
		String gameRoleId = getGameRoleId(gameId, roleId);
		RoleGameInfo roleGameInfo = new RoleGameInfo();
		roleGameInfo.roleId = roleId;
		roleGameInfo.gameRoleId = gameRoleId;

		return roleGameInfo;
	}

	@Override
	public GeneratedMessage joinGame(Role role, String lockString) {
		Integer gameId = GameCache.getGameLockStringMap().get(lockString);
		if (gameId == null) {
			return SC
					.newBuilder()
					.setMatchJoinGameResponse(
							MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber()))
					.build();
		}

		Game game = GameCache.getGameMap().get(gameId);
		if (game == null) {
			return SC
					.newBuilder()
					.setMatchJoinGameResponse(
							MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber()))
					.build();
		}
		String targetLock = game.getLockString();
		// 如果锁相同则可以进
		if (!targetLock.equals(lockString)) {
			return SC
					.newBuilder()
					.setMatchJoinGameResponse(
							MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.MATCH_ERROR_LOCK.getNumber()))
					.build();
		}

		if (game.getRoleIdMap().size() >= game.getMaxRoleCount()) {
			return SC
					.newBuilder()
					.setMatchJoinGameResponse(
							MatchJoinGameResponse.newBuilder().setErrorCode(ErrorCode.MATCH_MAX_ROLE_COUNT.getNumber()))
					.build();
		}

		RoleGameInfo roleGameInfo = this.createRoleGameInfo(role.getRoleId(), gameId);
		roleGameInfo.seatIndex = game.getRoleIdMap().size();
		game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);

		GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo);
		SC scJoinGame = SC.newBuilder()
				.setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(myGameRoleData)).build();
		// 通知其他人加入房间
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			SessionUtils.sc(info.roleId, scJoinGame);
		}

		List<GameRoleData> gameRoleDataList = new ArrayList<>(game.getRoleIdMap().size());
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			GameRoleData gameRoleData = this.parseGameRoleData(info);
			gameRoleDataList.add(gameRoleData);
		}
		return SC.newBuilder()
				.setMatchJoinGameResponse(MatchJoinGameResponse.newBuilder().addAllGameRoleData(gameRoleDataList))
				.build();
	}

	public GeneratedMessage match(Role role) {

		return null;
	}

	private GameRoleData parseGameRoleData(RoleGameInfo info) {
		Role role = loginService.getRoleById(info.roleId);
		String name = role != null ? role.getName() : "";

		return GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready).setSeated(info.seatIndex)
				.setName(name).build();
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
	private String getAIGameRoleId(int gameId, int roleId) {
		Game game = GameCache.getGameMap().get(gameId);
		int aiCount = 0;
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			if (roleGameInfo.roleId == 0) {
				aiCount++;
			}
		}
		return gameId + "_" + roleId + "_" + aiCount;
	}

	private String getLockString() {
		return "1980";
	}

}
