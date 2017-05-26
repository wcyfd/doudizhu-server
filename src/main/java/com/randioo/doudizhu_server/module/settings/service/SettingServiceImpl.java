package com.randioo.doudizhu_server.module.settings.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.dao.RoleDao;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.protocol.Settings.SettingsResponse;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;

@Service("settingService")
public class SettingServiceImpl extends ObserveBaseService implements SettingService {

	@Autowired
	private RoleDao roleDao;

	@Override
	public GeneratedMessage saveSettings(Role role, int volume, int musicVolume) {
		// TODO Auto-generated method stub
		roleDao.updateVolume(volume, musicVolume, role.getRoleId());
		return SC.newBuilder().setSettingsResponse(SettingsResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber())).build();
	}



	
	

}
