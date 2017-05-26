package com.randioo.doudizhu_server;

import com.randioo.doudizhu_server.entity.po.RoleMatchRule;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.net.SpringContext;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;
import com.randioo.randioo_server_base.utils.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.system.GameServerInit;
import com.randioo.randioo_server_base.utils.system.GlobleConfig;
import com.randioo.randioo_server_base.utils.system.GlobleConfig.GlobleEnum;

/**
 * Hello world!
 *
 */
public class doudizhu_serverApp {
	public static void main(String[] args) {
		StringUtils.printArgs(args);
		GlobleConfig.init(args);

		SensitiveWordDictionary.readAll("./sensitive.txt");

		SpringContext.initSpringCtx("ApplicationContext.xml");

		((GameServerInit) SpringContext.getBean("gameServerInit")).setHandler(new ServerHandler()).start();
		GlobleConfig.set(GlobleEnum.LOGIN, true);

		MatchModelService service = SpringContext.getBean("matchModelService");

		RoleMatchRule rule = new RoleMatchRule();
		rule.setId("1");
		int nowTime = TimeUtils.getNowTime();
		rule.setMatchTime(nowTime);
		rule.setMaxMatchCount(3);
		rule.setRoleId(1);
		rule.setWaitTime(12);

		service.matchRole(rule);
		new Thread(new Runnable() {

			private int i = 1;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(i);
					i++;
				}
			}

		}).start();

	}
}
