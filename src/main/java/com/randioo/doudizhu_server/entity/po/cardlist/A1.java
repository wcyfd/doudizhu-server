package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.List;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;

public class A1 extends CardList {
	private int num;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();

		return num - ((A1) arg0).num;
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 1)
			throw new CardListPatternException();
		int num = cardSort.getCardSort().get(0).iterator().next();
		A1 a = new A1();
		a.num = num;
		return a;
	}

}
