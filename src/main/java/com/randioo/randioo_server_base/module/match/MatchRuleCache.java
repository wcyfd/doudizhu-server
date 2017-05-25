package com.randioo.randioo_server_base.module.match;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MatchRuleCache {
	private static Map<String, MatchRule> matchRuleMap = new ConcurrentHashMap<>();

	public static Map<String, MatchRule> getMatchRuleMap() {
		return matchRuleMap;
	}

	public static Set<String> cancelMatchRuleIdSet = new HashSet<>();

	public static Set<String> getDeleteMatchRuleIdSet() {
		return cancelMatchRuleIdSet;
	}
}
