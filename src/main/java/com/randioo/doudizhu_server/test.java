package com.randioo.doudizhu_server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Entity.GameConfig;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.SpringContext;

public class test {
	public static void test(){
		final FightService fightService = SpringContext.getBean("fightService");
		
		
		Role host = new Role();
		host.setRoleId(111);
		host.setAccount("111");
		host.setName("111");
		
		Game game = new Game();
		GameConfig config = GameConfig.newBuilder().setDi(1).setMingpai(true).setMoguai(true).setRound(1).build();
		game.setGameConfig(config);
		game.setGameId(555555);
		game.setGameType(GameType.GAME_TYPE_FRIEND);
		game.setGameState(GameState.GAME_STATE_PREPARE);

		game.setMasterRoleId(host.getRoleId());
		game.setMaxRoleCount(3);

		RoleGameInfo roleGameInfo = new RoleGameInfo();
		roleGameInfo.roleId = host.getRoleId();
		roleGameInfo.gameRoleId =  game.getGameId()+"_"+host.getRoleId();		
		roleGameInfo.ready = true;
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
		roleGameInfo.ready = true;
		role2.setGameId(game.getGameId());
		RoleCache.putNewRole(role2);
		game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);
		
		roleGameInfo = new RoleGameInfo();
		roleGameInfo.roleId = role3.getRoleId();
		roleGameInfo.gameRoleId =  game.getGameId()+"_"+role3.getRoleId();		
		role3.setGameId(game.getGameId());
		RoleCache.putNewRole(role3);
		game.getRoleIdMap().put(roleGameInfo.gameRoleId, roleGameInfo);
		
		game.setGameState(GameState.GAME_START_START);
		if (game.getRoleIdList().size() == 0) {
			game.getRoleIdList().addAll(game.getRoleIdMap().keySet());
		}
		int [][]card = {
				{0x11, 0x21, 0x31, 0x41, 0x12, 0x22, 0x32, 0x42, 0x13, 0x23, 0x33, 0x43, 0x14, 0x24, 0x34, 0x44, 0x1D},
				{0x15, 0x25, 0x35, 0x45, 0x16, 0x26, 0x36, 0x46, 0x17, 0x27, 0x37, 0x47, 0x18, 0x28, 0x38, 0x48, 0x2D},
				{0x19, 0x29, 0x39, 0x49, 0x1A, 0x2A, 0x3A, 0x4A, 0x1B, 0x2B, 0x3B, 0x4B, 0x1C, 0x2C, 0x3C, 0x4C, 0x3D, 0x4D, 0x0E, 0x0F}
		};
		List<List<Integer>>cardlist = new ArrayList<>(3);
		List<Integer> cardlist1 = new ArrayList<>(20);
		List<Integer> cardlist2 = new ArrayList<>(20);
		List<Integer> cardlist3 = new ArrayList<>(20);
		for(int i = 0 ; i < card[0].length ; i ++){
			cardlist1.add(card[0][i]);
		}
		for(int i = 0 ; i < card[1].length ; i ++){
			cardlist2.add(card[1][i]);
		}
		for(int i = 0 ; i < card[2].length ; i ++){
			cardlist3.add(card[2][i]);
		}
		cardlist.add(cardlist1);
		cardlist.add(cardlist2);
		cardlist.add(cardlist3);
		GameCache.getGameMap().put(game.getGameId(), game);
		int j = 0;
		for (RoleGameInfo tRoleGameInfo : game.getRoleIdMap().values()){
			tRoleGameInfo.cards = cardlist.get(j);
			j++;
		}
		game.setLockString("222222");
		GameCache.getGameLockStringMap().put(game.getLockString(), game.getGameId());
		System.out.println("-----"+GameCache.getGameLockStringMap().get(game.getLockString())+"___"+game.getLockString());
		final Role testhost = host;
		final Role test2 = role2;
		final Role test3 = role3;
		final Game testGame = game;

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
				
				if(i == 5){
					/*fightService.exitGame(test2);
					Game game = GameCache.getGameMap().get(test2.getGameId());
					System.out.println(game == null?"null":"not null");
					if(game != null){
						System.out.println(game.getRoleIdMap().keySet());
					}*/
					for (RoleGameInfo roleGameInfo : testGame.getRoleIdMap().values()) {
						for(int i : roleGameInfo.cards)
							System.out.print(Integer.toHexString(i)+",");
						System.out.println("");
					}
					for(int i : testGame.getLandlordCards())
						System.out.print(Integer.toHexString(i)+",");
					System.out.println("");
					List<Integer> list = new ArrayList <> ();
					List<Integer> card = GameCache.getGameMap().get(testhost.getGameId()).getRoleIdMap().get(GameCache.getGameMap().get(testhost.getGameId()).getGameId()+"_"+testhost.getRoleId()).cards;
					list.add(card.get(0));
					list.add(card.get(1));
					list.add(card.get(2));
					list.add(card.get(6));
					list.add(card.get(4));
					list.add(card.get(5));
					//list.add(card.get(12));
					//list.add(card.get(13));
					fightService.sendCard(testhost, list);
					Class<?> clazz =  GameCache.getGameMap().get(testhost.getGameId()).getLastCardList().getClass();
					try {
						Method m = clazz.getMethod("getNum");
						int t = (int) m.invoke(GameCache.getGameMap().get(testhost.getGameId()).getLastCardList());
						System.out.println(Integer.toHexString(t));
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println(GameCache.getGameMap().get(testhost.getGameId()).getLastCardList());
					list.clear();
					card.clear();
					
					card = GameCache.getGameMap().get(test2.getGameId()).getRoleIdMap().get(GameCache.getGameMap().get(test2.getGameId()).getGameId()+"_"+test2.getRoleId()).cards;
					list.add(card.get(0));
					list.add(card.get(1));
					list.add(card.get(2));
					list.add(card.get(6));
					list.add(card.get(4));
					list.add(card.get(5));
					fightService.sendCard(test2, list);
					clazz =  GameCache.getGameMap().get(test2.getGameId()).getLastCardList().getClass();
					try {
						Method m = clazz.getMethod("getNum");
						int t = (int) m.invoke(GameCache.getGameMap().get(test2.getGameId()).getLastCardList());
						System.out.println(Integer.toHexString(t));
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(GameCache.getGameMap().get(test2.getGameId()).getLastCardList());
					list.clear();
					card.clear();
					
					card = GameCache.getGameMap().get(test3.getGameId()).getRoleIdMap().get(GameCache.getGameMap().get(test3.getGameId()).getGameId()+"_"+test3.getRoleId()).cards;
					list.add(card.get(8));
					list.add(card.get(9));
					//list.add(card.get(17));
					//list.add(card.get(16));
					list.add(card.get(10));
					list.add(card.get(11));
					//list.add(card.get(18));
					//list.add(card.get(19));
					fightService.sendCard(test3, list);
					clazz =  GameCache.getGameMap().get(test3.getGameId()).getLastCardList().getClass();
					try {
						Method m = clazz.getMethod("getNum");
						int t = (int) m.invoke(GameCache.getGameMap().get(test3.getGameId()).getLastCardList());
						System.out.println(Integer.toHexString(t));
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(GameCache.getGameMap().get(test3.getGameId()).getLastCardList());
					
				}
				/*if(i == 25){
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
				}*/
				i++;
				}
				
				
			}
			
		}).start();;
	}
}
