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
		}
		if(game.getGameType() == GameType.GAME_TYPE_MATCH){			
			int []fen = new int[3];
			int i = 0;
			int landlordIndex = 0;
			for(RoleGameInfo info : game.getRoleIdMap().values()){	
				if(info.gameRoleId.equals(game.getLandlordGameRoleId())){
					landlordIndex = i;
					Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
					if(role == null){
						fen[i] = info.allMark;
					}
					if(role != null){
						fen[i] = info.allMark * (landLordWin ? 1 : -1);
						
						if(role.getMoney() < fen[i]){
							fen[i] = role.getMoney() ;
						}
						fen[i] *=(landLordWin ? 1 : -1);

					}
				}
				else{
					Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
					if(role == null){
						fen[i] = info.allMark;
					}
					if(role != null){
						fen[i] = info.allMark * (landLordWin ? -1 : 1);
						
						if(role.getMoney() < fen[i]){
							fen[i] = role.getMoney();
						}
						fen[i] *=(landLordWin ? -1 : 1);
					}
				}
				i++;
			}
			int cal = 0;
			for(int temp : fen){
				cal += temp;
			}
			if((cal <= 0 && fen[landlordIndex] < 0)||(cal > 0 && fen[landlordIndex] > 0)){
				int all = 0;
				for(int t = 0 ; t < fen.length ; t ++){
					if(t != landlordIndex){
						all += fen[t];
					}
				}
				fen[landlordIndex] = all*(-1);
			}else if(cal <= 0 && fen[landlordIndex] > 0){
				if(fen[landlordIndex] % 2 == 1){
					fen[landlordIndex] -= 1;
				}
				for(int t = 0 ; t < fen.length ; t ++){
					if(t != landlordIndex){
						fen[t] = fen[landlordIndex]/2*(-1) < fen[t] ? fen[t] : fen[landlordIndex]/2*(-1);
					}
				}
				int all = 0;
				for(int t = 0 ; t < fen.length ; t ++){
					if(t != landlordIndex){
						all += fen[t];
					}
				}
				fen[landlordIndex] = all*(-1);				
			}else if(cal > 0 && fen[landlordIndex] < 0){
				if(fen[landlordIndex] % 2 == 1){
					fen[landlordIndex] += 1;
				}
				for(int t = 0 ; t < fen.length ; t ++){
					if(t != landlordIndex){
						fen[t] = fen[landlordIndex]/2*(-1);
					}
				}				
			}
			System.out.print("@@@fen:");
			for(int t :fen){
				System.out.print(t+"--");
			}
			System.out.println("");
			i = 0;
			for(RoleGameInfo info : game.getRoleIdMap().values()){
				Role role = (Role) RoleCache.getRoleMap().get(info.roleId);
				if(role != null){
					role.setMoney(role.getMoney() + fen[i]);
					info.allMark = info.currentMark = fen[i];
				}				
				i++;
			}
		}
		
		
	}
}
