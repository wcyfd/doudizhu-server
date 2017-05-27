package com.randioo.doudizhu_server.entity.bo;

import java.util.HashMap;
import java.util.Map;

import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;

public class Game {
	private int gameId;
	// 玩家id集合
	private Map<String, RoleGameInfo> roleIdMap = new HashMap<>();
	// 房主id
	private int masterRoleId;
	// 房间锁
	private String lockString;
	// 最大玩家数量
	private int maxRoleCount;
	// 游戏开始
	private GameState gameState;
	// 游戏类型
	private GameType gameType;

	private int round = 0;
	private int di = 0;

	private boolean moguai = false;
	private boolean mingpai = false;
	
	// 在线玩家数量
	private int onlineRoleCount;

	public int getOnlineRoleCount() {
		return onlineRoleCount;
	}

	public void setOnlineRoleCount(int onlineRoleCount) {
		this.onlineRoleCount = onlineRoleCount;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getDi() {
		return di;
	}

	public void setDi(int di) {
		this.di = di;
	}

	public boolean isMoguai() {
		return moguai;
	}

	public void setMoguai(boolean moguai) {
		this.moguai = moguai;
	}

	public boolean isMingpai() {
		return mingpai;
	}

	public void setMingpai(boolean mingpai) {
		this.mingpai = mingpai;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	public Map<String, RoleGameInfo> getRoleIdMap() {
		return roleIdMap;
	}

	public int getMasterRoleId() {
		return masterRoleId;
	}

	public void setMasterRoleId(int masterRoleId) {
		this.masterRoleId = masterRoleId;
	}

	public String getLockString() {
		return lockString;
	}

	public void setLockString(String lockString) {
		this.lockString = lockString;
	}

	public int getMaxRoleCount() {
		return maxRoleCount;
	}

	public void setMaxRoleCount(int maxRoleCount) {
		this.maxRoleCount = maxRoleCount;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

}
