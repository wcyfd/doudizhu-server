package com.randioo.doudizhu_server.entity.bo;

import com.randioo.randioo_server_base.db.DataEntity;

public class VideoData extends DataEntity{
	private int id;
	private int roleId;
	private int gameId;
	private byte[] video;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public byte[] getVideo() {
		return video;
	}
	public void setVideo(byte[] video) {
		this.video = video;
	}
}
