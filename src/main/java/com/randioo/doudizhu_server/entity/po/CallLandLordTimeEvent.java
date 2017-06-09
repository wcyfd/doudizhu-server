package com.randioo.doudizhu_server.entity.po;

import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;

public abstract class CallLandLordTimeEvent extends DefaultTimeEvent {
	private int gameId;
	private String gameRoleId;
	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getGameRoleId() {
		return gameRoleId;
	}

	public void setGameRoleId(String gameRoleId) {
		this.gameRoleId = gameRoleId;
	}
	
	
}
