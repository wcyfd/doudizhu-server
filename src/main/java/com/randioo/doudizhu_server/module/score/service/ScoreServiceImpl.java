package com.randioo.doudizhu_server.module.score.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.entity.po.cardlist.CardList;
import com.randioo.doudizhu_server.module.fight.FightConstant;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;

@Service("scoreService")
public class ScoreServiceImpl extends ObserveBaseService implements ScoreService {

	@Autowired
	private FightService fightService;

	@Override
	public void initService() {
		fightService.addObserver(this);
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (msg.equals(FightConstant.SEND_CARD)) {
			/*int gameId = (int) args[0];
			String gameRoleId = (String) args[1];
			CardList sendCardList = (CardList) args[2];

			this.updateScore(gameId, gameRoleId);*/
		}
	}
	@Override
	public void updateScore(int gameId, String gameRoleId) {
		// 每次出牌后,计算分数
		Game game = GameCache.getGameMap().get(gameId);
		boolean landLordWin = gameRoleId.equals(game.getLandlordGameRoleId());

		int mark = (int) (game.getCallLandlordScore() * game.getGameConfig().getDi() * Math.pow(2, game.getMultiple()))*(landLordWin?(game.isLandLordSpring()?2:1):(game.isFarmerSpring()?2:1));
		System.out.println("Mark:"+mark+"Times:"+game.getMultiple());
		for(RoleGameInfo info : game.getRoleIdMap().values()){
			info.currentMark = mark*(game.getLandlordGameRoleId().equals(info.gameRoleId) ? (landLordWin ? 2 : -2) : (landLordWin ? -1 : 1));
			info.allMark += info.currentMark;
			if(game.getGameType() == GameType.GAME_TYPE_MATCH){
				Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
				if(role != null){
					role.setMoney(role.getMoney() + info.currentMark);
				}
			}
		}
		
		
	}
}
