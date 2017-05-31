package com.randioo.doudizhu_server.entity.bo;

public class Role extends RoleInterfaceImpl {

	private int money;
	private int gameId;
	private int sex;
	private int volume = 0;
	private int musicVolume = 0;
	private String account;
	private int roleId;
	private String name;
	private String loginTimeStr;
	private String offlineTimeStr;
	private String createTimeStr;
	private String moneyExchangeTimeStr;
	private String loadTimeStr;
	private String headImgUrl;
	private int randiooMoney;
	private int moneyExchangeNum;

	public int getRandiooMoney() {
		return randiooMoney;
	}

	public void setRandiooMoney(int randiooMoney) {
		this.randiooMoney = randiooMoney;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	@Override
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@Override
	public int getRoleId() {
		return this.roleId;
	}

	@Override
	public String getAccount() {
		return this.account;
	}

	@Override
	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setLoginTimeStr(String loginTimeStr) {
		this.loginTimeStr = loginTimeStr;
	}

	@Override
	public String getLoginTimeStr() {
		return this.loginTimeStr;
	}

	@Override
	public void setOfflineTimeStr(String offlineTimeStr) {
		this.offlineTimeStr = offlineTimeStr;
	}

	@Override
	public String getOfflineTimeStr() {
		return this.offlineTimeStr;
	}

	@Override
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	@Override
	public String getCreateTimeStr() {
		return this.createTimeStr;
	}

	@Override
	public void setLoadTimeStr(String loadTimeStr) {
		
		this.loadTimeStr = loadTimeStr;
	}

	@Override
	public String getLoadTimeStr() {
		return this.loadTimeStr;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(int musicVolume) {
		this.musicVolume = musicVolume;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		setChange(true);
		this.money = money;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getMoneyExchangeTimeStr() {
		return moneyExchangeTimeStr;
	}

	public void setMoneyExchangeTimeStr(String moneyExchangeTimeStr) {
		this.moneyExchangeTimeStr = moneyExchangeTimeStr;
	}

	public int getMoneyExchangeNum() {
		return moneyExchangeNum;
	}

	public void setMoneyExchangeNum(int moneyExchangeNum) {
		this.moneyExchangeNum = moneyExchangeNum;
	}

}
