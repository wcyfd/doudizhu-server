package com.randioo.doudizhu_server;

import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.net.SpringContext;

public class test {
	public static void test(){
		final FightService fightService = SpringContext.getBean("fightService");
		
		Role host = new Role();
		host.setRoleId(111);
		host.setAccount("111");
		host.setName("111");
		
		Game game = new Game();
		game.setGameId(555555);
		game.setGameType(GameType.GAME_TYPE_FRIEND);
		game.setGameState(GameState.GAME_STATE_PREPARE);

		game.setMasterRoleId(host.getRoleId());
		game.setMaxRoleCount(3);

		RoleGameInfo roleGameInfo = new RoleGameInfo();
		roleGameInfo.roleId = host.getRoleId();
		roleGameInfo.gameRoleId =  game.getGameId()+"_"+host.getRoleId();		
		roleGameInfo.seatIndex = game.getRoleIdMap().size();
		host.setGameId(game.getGameId());
		RoleCache.putNewRole(host);
		game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);
		
		Role role2 = new Role();
		Role role3 = new Role();
		role2.setRoleId(222);
		role3.setRoleId(333);
		role2.setAccount("222");
		role3.setAccount("333");
		role2.setName("222");
		role3.setName("333");
		
		roleGameInfo = new RoleGameInfo();
		roleGameInfo.roleId = role2.getRoleId();
		roleGameInfo.gameRoleId =  game.getGameId()+"_"+role2.getRoleId();		
		roleGameInfo.seatIndex = game.getRoleIdMap().size();
		role2.setGameId(game.getGameId());
		RoleCache.putNewRole(role2);
		game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);
		
		roleGameInfo = new RoleGameInfo();
		roleGameInfo.roleId = role3.getRoleId();
		roleGameInfo.gameRoleId =  game.getGameId()+"_"+role3.getRoleId();		
		roleGameInfo.seatIndex = game.getRoleIdMap().size();
		role3.setGameId(game.getGameId());
		RoleCache.putNewRole(role3);
		game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);
		
		game.setGameState(GameState.GAME_START_START);


		
		GameCache.getGameMap().put(game.getGameId(), game);
		//GameCache.getGameLockStringMap().put(game.getLockString(), game.getGameId());
		final Role testhost = host;
		final Role test2 = role2;
		final Role test3 = role3;

		new Thread(new Runnable(){

			private int i = 0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(i == 10){
					fightService.exitGame(test2);
					Game game = GameCache.getGameMap().get(test2.getGameId());
					System.out.println(game == null?"null":"not null");
					if(game != null){
						System.out.println(game.getRoleIdMap().keySet());
					}
				}
				if(i == 25){
					fightService.exitGame(test2);
					Game game = GameCache.getGameMap().get(test2.getGameId());
					System.out.println(game == null?"null":"not null");
					if(game != null){
						System.out.println(game.getRoleIdMap().keySet());
					}
				}
				if(i == 20){
					fightService.agreeExit(testhost, false);
					Game game = GameCache.getGameMap().get(testhost.getGameId());
					System.out.println(game == null?"null":"not null");
					if(game != null){
						System.out.println(game.getRoleIdMap().keySet());
					}
				}
				if(i == 30){
					fightService.agreeExit(test3, true);
					Game game = GameCache.getGameMap().get(test3.getGameId());
					System.out.println(game == null?"null":"not null");
					if(game != null){
						System.out.println(game.getRoleIdMap().keySet());
					}
				}
				i++;
				}
				
				
			}
			
		}).start();;
	}
}
