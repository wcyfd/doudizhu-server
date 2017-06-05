package com.randioo.doudizhu_server;

import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.template.EntityRunnable;
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
	public static void asynManipulate(Role role) {

		System.out.println("[account:" + role.getAccount() + ",name:" + role.getName() + "] manipulate");

		SessionCache.removeSessionById(role.getRoleId());
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

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("id:" + role.getRoleId() + ",account:" + role.getAccount() + ",name:" + role.getName()
					+ "] save error");
		}

	}

}
