package com.randioo.doudizhu_server.module.score.service;

import org.springframework.stereotype.Service;

import com.randioo.randioo_server_base.utils.Observer;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;

@Service("scoreService")
public class ScoreServiceImpl extends ObserveBaseService implements ScoreService {
	@Override
	public void update(Observer observer, String msg, Object... args) {
		
	}
}
