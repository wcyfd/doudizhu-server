package com.randioo.randioo_server_base.module.match;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;

import com.randioo.randioo_server_base.db.DBRunnable;
import com.randioo.randioo_server_base.module.BaseService;
import com.randioo.randioo_server_base.module.match.MatchRule.MatchState;
import com.randioo.randioo_server_base.net.CacheLockUtil;
import com.randioo.randioo_server_base.utils.TimeUtils;
import com.randioo.randioo_server_base.utils.scheduler.EventScheduler;

public class MatchModelServiceImpl extends BaseService implements MatchModelService {

	private ExecutorService executor = null;

	@Autowired
	private EventScheduler eventScheduler;

	private MatchHandler matchHandler;

	@Override
	public void initService() {
		executor = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void setMatchHandler(MatchHandler matchHandler) {
		this.matchHandler = matchHandler;
	}

	@Override
	public void matchRole(MatchRule matchRule) {
		matchRule.setState(MatchState.MATCH_READY);
		// 添加匹配信息
		MatchRuleCache.getMatchRuleMap().put(matchRule.getId(), matchRule);
		// 设置超时定时器
		executor.submit(new DBRunnable<MatchRule>(matchRule) {

			@Override
			public void run(MatchRule entity) {
				int maxCount = entity.getMaxMatchCount();
				try {
					// 由于异步加入，所以加入之前先检查一次是否可以删除
					Lock lock = getLock(entity.getId());
					try {
						lock.lock();
						if (checkDelete(entity)) {
							return;
						}
					} finally {
						lock.unlock();
					}

					Map<String, MatchRule> tempMap = getTempMapByCount(maxCount);
					tempMap.put(entity.getId(), entity);

					Set<String> matchRuleIdSet = new HashSet<>(MatchRuleCache.getMatchRuleMap().keySet());

					boolean matchSuccess = false;
					for (String id : matchRuleIdSet) {
						MatchRule matchRule = MatchRuleCache.getMatchRuleMap().get(id);
						// 不能匹配自己
						if (matchRule.getId().equals(entity.getId()))
							continue;

						// 先检查规则，通过了在考虑同步问题
						if (!checkMatch(entity, matchRule)) {
							continue;
						}

						// 检查是否要删除,检查第二次,主要还是为了提高锁同步的必要性,由于检查匹配规则的耗时可能非常的长,但是玩家可以随取消匹配的
						// 只检查自己是否取消匹配，如果取消匹配了，则下面的人不可能匹配上，直接下一个人
						if (checkDelete(entity))
							break;

						// 规则匹配没有问题则加入到缓存
						tempMap.put(matchRule.getId(), matchRule);

						Set<Lock> locks = new HashSet<>(getLocks(maxCount));
						try {
							lockSet_Lock(locks);

							// 如果人数到了,检查所有选中的人
							if (tempMap.size() < maxCount) {
								continue;
							}

							boolean needDelete = false;
							for (MatchRule rule : tempMap.values()) {
								needDelete = checkDelete(rule);
								tempMap.remove(rule.getId());
							}

							// 如果有要删除的则再找下一个人
							if (needDelete)
								continue;

							// 匹配成功
							for (MatchRule rule : tempMap.values())
								rule.setState(MatchState.MATCH_SUCCESS);

							break;

						} finally {
							lockSet_Unlock(locks);
						}
					}
					if (matchSuccess)
						matchHandler.matchSuccess(new HashMap<>(tempMap));

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// 删除作废的匹配
					for (String ruleId : MatchRuleCache.getDeleteMatchRuleIdSet())
						MatchRuleCache.getMatchRuleMap().remove(ruleId);

					getTempMapByCount(maxCount).clear();
					getTempLocks(maxCount).clear();
				}
			}
		});

		// 如果没有设置等待时间则不进行倒计时
		if (matchRule.getWaitTime() == 0)
			return;

		MatchTimeEvent timeEvent = new MatchTimeEvent(matchRule) {

			@Override
			public void outOfTime(MatchRule matchRule) {
				matchHandler.outOfTime(matchRule);
			}
		};

		int endTime = TimeUtils.getNowTime() + matchRule.getWaitTime();
		timeEvent.setEndTime(endTime);
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
	public void cancelMatch(String ruleId) {
		MatchRule matchRule = MatchRuleCache.getMatchRuleMap().get(ruleId);

		if (checkDelete(matchRule))
			return;
		Lock lock = getLock(ruleId);
		try {
			lock.lock();

			if (checkDelete(matchRule))
				return;

			matchRule.setState(MatchState.MATCH_CANCEL);
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 获得临时的匹配表
	 * 
	 * @param matchCount
	 * @return
	 * @author wcy 2017年5月26日
	 */
	private Map<String, MatchRule> getTempMapByCount(int matchCount) {
		Map<Integer, Map<String, MatchRule>> matchTempMap = MatchRuleCache.getMatchTempMap();
		Map<String, MatchRule> matchRuleMap = matchTempMap.get(matchCount);
		if (matchRuleMap == null) {
			matchRuleMap = new HashMap<>();
			matchTempMap.put(matchCount, matchRuleMap);
		}
		return matchRuleMap;
	}

	private Set<Lock> getTempLocks(int matchCount) {
		Map<Integer, Set<Lock>> tempLocksMap = MatchRuleCache.getLocksTempMap();
		Set<Lock> lockSet = tempLocksMap.get(matchCount);
		if (lockSet == null) {
			lockSet = new HashSet<>();
			tempLocksMap.put(matchCount, lockSet);
		}

		return lockSet;
	}

	/**
	 * 获得锁
	 * 
	 * @param id
	 * @return
	 * @author wcy 2017年5月26日
	 */
	private Lock getLock(String id) {
		return CacheLockUtil.getLock(MatchRule.class, id);
	}

	private Set<Lock> getLocks(int maxCount) {
		Map<String, MatchRule> map = getTempMapByCount(maxCount);
		Set<Lock> set = this.getTempLocks(maxCount);
		for (MatchRule matchRule : map.values()) {
			Lock lock = getLock(matchRule.getId());
			set.add(lock);
		}
		return set;
	}

	private void lockSet_Lock(Set<Lock> locks) {
		for (Lock lock : locks)
			lock.lock();

	}

	private void lockSet_Unlock(Set<Lock> locks) {
		for (Lock lock : locks) {
			lock.unlock();
		}
	}

	private boolean checkMatch(MatchRule rule1, MatchRule rule2) {
		if (rule1.getMaxMatchCount() != rule2.getMaxMatchCount()) {
			return false;
		}
		return matchHandler.checkMatch(rule1, rule2);
	}

}
