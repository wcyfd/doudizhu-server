package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;

public class A4 extends A1 {
	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			return 1;

		return getNum() - ((A4) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 4)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(3);
		if (set.size() == 0)
			throw new CardListPatternException();

		int num = set.iterator().next();

		A4 a = new A4();
		a.setNum(num);
		return a;
	}

}
