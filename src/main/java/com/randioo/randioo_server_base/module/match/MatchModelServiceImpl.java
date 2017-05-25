package com.randioo.randioo_server_base.module.match;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

import com.randioo.randioo_server_base.db.DBRunnable;
import com.randioo.randioo_server_base.module.BaseService;
import com.randioo.randioo_server_base.module.match.MatchRule.MatchState;
import com.randioo.randioo_server_base.utils.scheduler.EventScheduler;

public class MatchModelServiceImpl extends BaseService implements MatchModelService {

	private ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	@Autowired
	private EventScheduler eventScheduler;

	private MatchHandler matchHandler;

	@Override
	public void setMatchHandler(MatchHandler matchHandler) {
		this.matchHandler = matchHandler;
	}

	@Override
	public void matchRole(MatchRule matchRule) {
		// 添加匹配信息
		MatchRuleCache.getMatchRuleMap().put(matchRule.getId(), matchRule);
		// 设置超时定时器
		executor.submit(new DBRunnable<MatchRule>(matchRule) {

			@Override
			public void run(MatchRule entity) {
				try {
					for (MatchRule matchRule : MatchRuleCache.getMatchRuleMap().values()) {
						// 不能匹配自己
						if (matchRule.getId() == entity.getId())
							continue;

						// 检查是否要删除
						if (checkDelete(matchRule) || checkDelete(entity))
							continue;

						// 先检查规则，通过了在考虑同步问题
						boolean matchResult = matchHandler.checkMatch(entity, matchRule);
						if (!matchResult)
							continue;

						// 检查是否要删除,检查第二次,主要还是为了提高锁同步的必要性,由于检查匹配规则的耗时可能非常的长,但是玩家可以随取消匹配的
						if (checkDelete(matchRule) || checkDelete(entity))
							continue;
						synchronized (matchRule) {
							if (checkDelete(matchRule))
								continue;
							synchronized (entity) {
								if (checkDelete(entity))
									continue;
								// 没有匹配成功则继续匹配
								if (!matchResult)
									continue;

								entity.setState(MatchState.MATCH_SUCCESS);
								matchRule.setState(MatchState.MATCH_SUCCESS);
							}
						}
						matchHandler.matchSuccess(entity, matchRule);
					}

					// 删除作废的匹配
					for (String ruleId : MatchRuleCache.getDeleteMatchRuleIdSet()) {
						MatchRuleCache.getMatchRuleMap().remove(ruleId);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		MatchTimeEvent timeEvent = new MatchTimeEvent(matchRule) {

			@Override
			public void outOfTime(MatchRule matchRule) {
				matchHandler.outOfTime(matchRule);
			}
		};

		timeEvent.setEndTime(matchRule.getWaitTime());
		// 发送定时器
		eventScheduler.addEvent(timeEvent);
	}

	/**
	 * 检查是否删除
	 * 
	 * @param matchRule
	 * @return
	 */
	private boolean checkDelete(MatchRule matchRule) {
		if (matchRule == null)
			return true;

		MatchState state = matchRule.getState();
		if (state != MatchState.MATCH_CANCEL && state != MatchState.MATCH_SUCCESS)
			return false;

		MatchRuleCache.getDeleteMatchRuleIdSet().add(matchRule.getId());

		return true;
	}

	@Override
	public void cancelMatch(int ruleId) {
		MatchRule matchRule = MatchRuleCache.getMatchRuleMap().get(ruleId);

		if (checkDelete(matchRule))
			return;
		synchronized (matchRule) {
			if (checkDelete(matchRule))
				return;

			matchRule.setState(MatchState.MATCH_CANCEL);
		}
	}

}
