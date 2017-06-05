package com.randioo.doudizhu_server;

import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.config.GlobleConfig.GlobleEnum;
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.SpringContext;
import com.randioo.randioo_server_base.utils.StringUtils;

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

		//test.test();

	}

}
