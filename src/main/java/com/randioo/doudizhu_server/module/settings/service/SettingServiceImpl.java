package com.randioo.doudizhu_server.module.settings.service;

import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.protocol.Settings.GetSettingsResponse;
import com.randioo.doudizhu_server.protocol.Settings.SettingsResponse;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;

@Service("settingService")
public class SettingServiceImpl extends ObserveBaseService implements SettingService {

	@Override
	public GeneratedMessage saveSettings(Role role, int volume, int musicVolume) {
		role.setVolume(volume);
		role.setMusicVolume(musicVolume);
		return SC.newBuilder().setSettingsResponse(SettingsResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber())).build();
	}

	@Override
	public GeneratedMessage getSettings(Role role) {
		GetSettingsResponse.Builder GSRBuilder = GetSettingsResponse.newBuilder();
		if(role == null){
			GSRBuilder.setErrorCode(ErrorCode.NO_ROLE_DATA.getNumber());
		}
		else{
			GSRBuilder.setMusicVolume(role.getMusicVolume());
			GSRBuilder.setVolume(role.getVolume());
		}
		return SC.newBuilder().setGetSettingsResponse(GSRBuilder).build();
	}



	
	

}
