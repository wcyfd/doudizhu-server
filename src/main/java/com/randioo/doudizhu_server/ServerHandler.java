package com.randioo.doudizhu_server;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.protocol.ClientMessage.CS;
import com.randioo.doudizhu_server.util.Write2File;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.navigation.Navigation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.randioo_server_base.net.IoHandlerAdapter;
import com.randioo.randioo_server_base.utils.LogService;
import com.randioo.randioo_server_base.utils.TimeUtils;
import com.randioo.randioo_server_base.utils.template.Function;

public class ServerHandler extends IoHandlerAdapter {

//	private List<Function> actionChains = new ArrayList<>();
//
//	public ServerHandler() {
//		init();
//	}
//
//	public void init() {
//		actionChains.add(new NormalAction());
//	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("roleId:" + session.getAttribute("roleId") + " sessionCreated");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {				
		System.out.println("roleId:" + session.getAttribute("roleId") + " sessionOpened");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("roleId:" + session.getAttribute("roleId") + " sessionClosed");
		final Role role = (Role) RoleCache.getRoleBySession(session);

		try {
			if (role != null) {
				SessionCloseHandler.asynManipulate(role);
				// LogService.print(new Function() {
				//
				// @Override
				// public Object apply(Object... params) {
				// Write2File.write2File(role.getRoleId(), "offline");
				// return "";
				// }
				//
				// });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close(true);
		}

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable e) throws Exception {

	}

	@Override
	public void messageReceived(IoSession session, Object messageObj) throws Exception {

		InputStream input = (InputStream) messageObj;
		// input.mark(0);

		try {
			// for (Function func : actionChains) {
			// boolean result = (Boolean) func.apply(input, session);
			// if (result)
			// break;
			// }

			CS message = CS.parseDelimitedFrom(input);
			actionDispatcher(message, session);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				input.close();
			}
		}

	}

	/**
	 * 消息事件分发
	 * 
	 * @param message
	 * @param session
	 * @author wcy 2017年1月3日
	 */
	private void actionDispatcher(GeneratedMessage message, IoSession session) {
		Map<FieldDescriptor, Object> allFields = message.getAllFields();
		for (Map.Entry<FieldDescriptor, Object> entrySet : allFields.entrySet()) {

			String name = entrySet.getKey().getName();
			IActionSupport action = Navigation.getAction(name);
			try {
				action.execute(entrySet.getValue(), session);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Fake protocol：" + name);
				session.close(true);
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		if (!message.toString().contains("scFightKeyFrame") && !message.toString().contains("PingResponse")) {

			System.out.println(message);
			final IoSession _session = session;
			final Object _message = message;

			// LogService.print(new Function() {
			//
			// @Override
			// public Object apply(Object... params) {
			// String msg = getMessage(_message, _session);
			// return msg;
			// }
			// });
		}
	}

	private String getMessage(Object message, IoSession session) {
		Integer roleId = (Integer) session.getAttribute("roleId");
		String roleAccount = null;
		String roleName = null;
		if (roleId != null) {
			Role role = (Role) RoleCache.getRoleById(roleId);
			if (role != null) {
				roleAccount = role.getAccount();
				roleName = role.getName();
			}
		}

		InetSocketAddress remoteIP = ((InetSocketAddress) session.getRemoteAddress());
		// String address = ",ip=" + remoteIP.getAddress().getHostAddress() +
		// ":" + remoteIP.getPort();
		String address = "";
		StringBuilder sb = new StringBuilder();
		sb.append(TimeUtils.getDetailTimeStr()).append(" [roleId:").append(roleId).append(",account:")
				.append(roleAccount).append(",name:").append(roleName).append(address).append("] ").append(message);
		String output = sb.toString();
		if (output.length() < 120) {
			output = output.replaceAll("\n", " ").replace("\t", " ").replace("  ", "");
		}

		if (roleId != null) {
			Write2File.write2File(roleId, output);
		}
		return output;
		// return "";
	}

	private class NormalAction implements Function {

		@Override
		public Boolean apply(Object... objects) {
			InputStream input = (InputStream) objects[0];
			IoSession session = (IoSession) objects[1];

			boolean result = false;
			try {
				input.reset();
				CS message = CS.parseDelimitedFrom(input);

				if (message != null) {
					result = true;
					String msg = message.toString();

					if (!msg.contains("PingRequest")) {
						final IoSession _session = session;
						final CS _message = message;
						LogService.print(new Function() {

							@Override
							public Object apply(Object... params) {
								String msg = getMessage(_message, _session);
								// String msg = "";
								return msg;
							}
						});
					}
					actionDispatcher(message, session);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

	}

}
