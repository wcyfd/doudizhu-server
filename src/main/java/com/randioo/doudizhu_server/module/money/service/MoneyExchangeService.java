package com.randioo.doudizhu_server.module.money.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.protocol.MoneyExchange.MoneyExchangeRequest;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;

public interface MoneyExchangeService extends ObserveBaseServiceInterface {

	GeneratedMessage moneyExchange(Role role, MoneyExchangeRequest request);

}
