package com.randioo.doudizhu_server.module.video.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface VideoService extends ObserveBaseServiceInterface {

	GeneratedMessage VideoGet(Role role);

	GeneratedMessage VideoGetById(int id);

	GeneratedMessage VideoGetByRound(int id, int round);

}
