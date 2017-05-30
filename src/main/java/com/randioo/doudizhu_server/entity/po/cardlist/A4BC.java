package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;

public class A4BC extends A4 {

	private List<Integer> addNumList = new ArrayList<>();
	private int count;

	public List<Integer> getAddNumList() {
		return addNumList;
	}

	public void setAddNumList(List<Integer> addNumList) {
		this.addNumList = addNumList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();

		A4BC a4bc = (A4BC) arg0;
		if (getCount() != a4bc.getCount())
			throw new CardTypeComparableException();

		return getNum() - a4bc.getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 6 && arr.size() != 8)
			throw new CardListPatternException();

		Set<Integer> set0 = cardSort.getCardSort().get(0);
		Set<Integer> set1 = cardSort.getCardSort().get(1);
		Set<Integer> set3 = cardSort.getCardSort().get(3);
		if (set3.size() == 0)
			throw new CardListPatternException();
		int count = 0;
		// 如果是带对子

		int cardCount = set0.size() + set1.size();
		if (cardCount == 6 && set0.size() == set1.size()) {
			count = 2;
		} else if (cardCount == 4 && set0.size() == 3) {
			count = 1;
		} else
			throw new CardListPatternException();

		A4BC a4bc = new A4BC();
		a4bc.setNum(set3.iterator().next());
		a4bc.setCount(count);
		for (int i : set0) {
			if (i != a4bc.getNum()) {
				a4bc.getAddNumList().add(i);
			}
		}

		return a4bc;
	}

}
