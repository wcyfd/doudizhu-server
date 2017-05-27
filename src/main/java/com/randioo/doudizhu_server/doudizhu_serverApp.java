package com.randioo.doudizhu_server;

import com.randioo.randioo_server_base.net.SpringContext;
import com.randioo.randioo_server_base.utils.StringUtils;
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

	}

}
