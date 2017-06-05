package com.randioo.doudizhu_server.module.settings.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.settings.service.SettingService;
import com.randioo.doudizhu_server.protocol.Settings.GetSettingsRequest;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(GetSettingsRequest.class)
public class SettingShowAction implements IActionSupport {

	@Autowired
	private SettingService settingService;

	@Override
	public void execute(Object data, IoSession session) {
		GetSettingsRequest request = (GetSettingsRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage sc = settingService.getSettings(role);
		SessionUtils.sc(session, sc);
	}

}
