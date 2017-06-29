package com.randioo.doudizhu_server.entity.po;

import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;

public abstract class SendCardTimeEvent extends DefaultTimeEvent {

	private int gameId;
	private int round;
	private boolean first;
	private boolean mustPutOut;

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isMustPutOut() {
		return mustPutOut;
	}

	public void setMustPutOut(boolean mustPutOut) {
		this.mustPutOut = mustPutOut;
	}
	
	
}
