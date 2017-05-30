package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;

public class A3N1 extends A1 {

	private int addNum;

	public void setAddNum(int addNum) {
		this.addNum = addNum;
	}

	public int getAddNum() {
		return addNum;
	}

	@Override
	public int compareTo(CardList o) {
		if (o.getClass() == A4.class || o.getClass() == KQ.class)
			return -1;
		if (o.getClass() != getClass())
			throw new CardTypeComparableException();

		return getNum() - ((A3N1) o).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 4)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(2);
		Set<Integer> set2 = cardSort.getCardSort().get(3);
		if (set.size() == 1 && set2.size() == 0) {
			A3N1 a = new A3N1();
			a.setNum(set.iterator().next());
			a.setAddNum(set2.iterator().next());
			return a;
		}

		throw new CardListPatternException();
	}

}
