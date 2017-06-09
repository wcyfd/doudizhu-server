package com.randioo.doudizhu_server.entity.po;

import java.util.List;

public class CardRecord {
	public List<Integer> cards;
	public String gameRoleId;
	@Override
	public String toString() {
		if(cards == null)
			return "null";
		return cards.toString();
	}
	
}
