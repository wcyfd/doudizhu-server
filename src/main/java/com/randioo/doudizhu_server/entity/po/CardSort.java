package com.randioo.doudizhu_server.entity.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CardSort {
	private List<Set<Integer>> cardSort = new ArrayList<>();

	public List<Set<Integer>> getCardSort() {
		return cardSort;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Set<Integer> set : cardSort) {
			sb.append(i).append("=");
			for (int value : set)
				sb.append(value).append(" ");
			sb.append("\n");
			i++;
		}
		return sb.toString();
	}
}
