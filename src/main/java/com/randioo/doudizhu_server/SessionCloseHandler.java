package com.randioo.doudizhu_server;

import org.springframework.beans.factory.annotation.Autowired;

import com.randioo.doudizhu_server.cache.local.GameCache;
import com.randioo.doudizhu_server.dao.RoleDao;
import com.randioo.doudizhu_server.entity.bo.Game;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.po.RoleGameInfo;
import com.randioo.doudizhu_server.module.fight.service.FightService;
import com.randioo.doudizhu_server.protocol.Entity.GameState;
import com.randioo.doudizhu_server.protocol.Entity.GameType;
import com.randioo.doudizhu_server.protocol.Fight.SCFightDisconnect;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.utils.SaveUtils;
import com.randioo.randioo_server_base.utils.SpringContext;
import com.randioo.randioo_server_base.utils.TimeUtils;

/**
 * session关闭角色数据处理
 * 
 */
public class SessionCloseHandler {
	/**
	 * 移除session缓存
	 * 
	 * @param id
	 */
	@Autowired
	private RoleDao roleDao;
	
	public static void asynManipulate(Role role) {

		System.out.println("[account:" + role.getAccount() + ",name:" + role.getName() + "] manipulate");

		MatchModelService matchModelService = SpringContext.getBean("matchModelService");
		if(role.getMatchRuleId() != null){
			matchModelService.cancelMatch(role.getMatchRuleId());
		}
		roleInGameOption(role);
		
		role.setOfflineTimeStr(TimeUtils.getDetailTimeStr());
		GameDB gameDB = SpringContext.getBean("gameDB");
		if (!gameDB.isUpdatePoolClose()) {
			gameDB.getUpdatePool().submit(new EntityRunnable<Role>(role) {
				@Override
				public void run(Role role) {
					
					roleDataCache2DB(role, true);
					
				}
			});
		}
	}

	public static void roleDataCache2DB(Role role, boolean mustSave) {
		try {
			if(SaveUtils.needSave(role, mustSave)){
				RoleDao roleDao = SpringContext.getBean("roleDao");
				roleDao.update(role);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("id:" + role.getRoleId() + ",account:" + role.getAccount() + ",name:" + role.getName()
					+ "] save error");
		}

	}
	public static void roleInGameOption(Role role){
		if(role.getGameId() == 0){
			return;
		}
		Game game = GameCache.getGameMap().get(role.getGameId());
		if(game == null){
			return;
		}
		RoleGameInfo myInfo = null;
		for(RoleGameInfo info : game.getRoleIdMap().values()){
			if(info.roleId == role.getRoleId()){
				myInfo = info;
				myInfo.online = false;
				break;
			}
		}
		FightService fightService = SpringContext.getBean("fightService");
		if(game.getGameState() == GameState.GAME_STATE_PREPARE && game.getGameType() == GameType.GAME_TYPE_MATCH){
			fightService.disconnectTimeUp(role.getRoleId());
		}else if(game.getGameState() == GameState.GAME_START_START){
			if(game.getCallLandlordCount() < game.getMaxRoleCount() && game.getCallLandlordScore() < 3 && game.getGameType() == GameType.GAME_TYPE_MATCH){
				if(game.getRoleIdMap().get(game.getRoleIdList().get(game.getCurrentRoleIdIndex())).roleId == role.getRoleId()){
					
					fightService.callLandlord(game.getGameId(), myInfo.gameRoleId, 0);
				}
			}
		}
		//TODO 通知断线
		
		for(RoleGameInfo info : game.getRoleIdMap().values()){
			if(info.roleId == role.getRoleId()){
				continue;
			}
			SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightDisconnect(SCFightDisconnect.newBuilder().setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId))).build());
			fightService.notifyObservers("RECORD", info, SC.newBuilder().setSCFightDisconnect(SCFightDisconnect.newBuilder().setSeated(game.getRoleIdList().indexOf(myInfo.gameRoleId))).build());
		}
	}

}
