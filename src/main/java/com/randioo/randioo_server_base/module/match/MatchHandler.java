package com.randioo.randioo_server_base.module.match;

public interface MatchHandler {
	public void outOfTime(MatchRule matchRule);

	public boolean checkMatch(MatchRule rule1, MatchRule rule2);

	public void matchSuccess(MatchRule rule1, MatchRule rule2);
}
