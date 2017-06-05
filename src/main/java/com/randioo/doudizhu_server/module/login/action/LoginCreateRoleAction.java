package com.randioo.doudizhu_server.module.login.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.module.login.service.LoginService;
import com.randioo.doudizhu_server.protocol.Login.LoginCreateRoleRequest;
import com.randioo.doudizhu_server.util.SessionUtils;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(LoginCreateRoleRequest.class)
public class LoginCreateRoleAction implements IActionSupport {

	@Autowired
	private LoginService loginService;

	@Override
	public void execute(Object data, IoSession session) {
		LoginCreateRoleRequest request = (LoginCreateRoleRequest) data;
		String account = request.getAccount();
		GeneratedMessage sc = loginService.creatRole(account);
		
		SessionUtils.sc(session, sc);

	}

}
