package com.randioo.randioo_server_base.module.match;

import java.util.Map;

public interface MatchHandler {
	public void outOfTime(MatchRule matchRule);

	public boolean checkMatch(MatchRule rule1, MatchRule rule2);

	public void matchSuccess(Map<String, MatchRule> matchRule);
}
