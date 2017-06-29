package com.randioo.doudizhu_server;

import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.randioo.doudizhu_server.dao.RoleDao;
import com.randioo.doudizhu_server.dao.VideoDao;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.entity.bo.VideoData;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.VideoUtils;
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
		GlobleConfig.set("URL", GlobleConfig.Boolean(GlobleEnum.DEBUG)?"http://10.0.51.6/APPadmin":"http://manager.app.randioo.com");

		SensitiveWordDictionary.readAll("./sensitive.txt");

		SpringContext.initSpringCtx("ApplicationContext.xml");

		((GameServerInit) SpringContext.getBean("gameServerInit")).setHandler(new ServerHandler()).start();
		GlobleConfig.set(GlobleEnum.LOGIN, true);
		
		/*RoleCache.putNewRole(createRole(1));
		RoleCache.putNewRole(createRole(2));
		RoleCache.putNewRole(createRole(3));
		MatchService matchService = SpringContext.getBean("matchService");
		matchService.match((Role) RoleCache.getRoleById(1));
		matchService.match((Role) RoleCache.getRoleById(2));
		matchService.match((Role) RoleCache.getRoleById(3));*/
		
		/*VideoDao videoDao = SpringContext.getBean("videoDao");
		System.out.println(VideoUtils.parseVideo(videoDao.getById(18)));
		try {
			System.out.println(SC.parseFrom(VideoUtils.parseVideo(videoDao.getById(18)).getVideoRecord(0)));
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VideoData vd = videoDao.getById(18);
		if(vd == null){
			System.out.println("NULL");
		}
		else 
			System.out.println(VideoUtils.parseVideo(vd).getVideoRecordList().size()+"---roomId:"+VideoUtils.parseVideo(vd).getRoomId()+"---id:"+vd.getId()+"---roleId:"+vd.getRoleId());
		*/
	}
	
	private static Role createRole(int i){
		Role role = new Role();
		role.setRoleId(i);
		role.setName("test"+i);
		role.setAccount("account"+i);
		return role;
	}

}
