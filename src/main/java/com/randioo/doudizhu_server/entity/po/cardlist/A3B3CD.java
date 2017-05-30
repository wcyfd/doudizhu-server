package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.util.CardTools;

public class A3B3CD extends A3B3 {

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

		A3B3CD a3b3cd = (A3B3CD) arg0;
		if (getLength() != a3b3cd.getLength())
			throw new CardTypeComparableException();

		return getNum() - ((A3B3CD) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (((arr.size() >= 8) && (arr.size() - 8) % 4 != 0) || ((arr.size() >= 10) && (arr.size() - 10) % 5 != 0))
			throw new CardListPatternException();

		Set<Integer> set2 = cardSort.getCardSort().get(2);
		if (set2.size() < 2)
			throw new CardListPatternException();
		cardSort = cardSort.clone();

		Set<Integer> set0 = cardSort.getCardSort().get(0);
		Set<Integer> set1 = cardSort.getCardSort().get(1);
		set2 = cardSort.getCardSort().get(2);

		int tmp = 0;
		int first = 0;
		int len = 0;
		for (int value : set2) {
			if (tmp == 0) {
				// 如果第三行数组中第一个数字是大于Q则就没有可能了
				if (value > CardTools.C_Q)
					throw new CardListPatternException();

				first = value;
				tmp = value;
			} else {
				if (value > CardTools.C_A)
					break;
			}
			len++;
		}
		if (len < 2)
			throw new CardListPatternException();

		for (int n = first; n < len; n++)
			CardTools.rmValue(cardSort, n, 3);

		int count = 0;

		if (set0.size() == len) {
			if (set1.size() != 0 && set1.size() != len)
				throw new CardListPatternException();

			count = set1.size() == len ? 2 : 1;
		} else {
			throw new CardListPatternException();
		}

		A3B3CD a = new A3B3CD();
		a.setNum(first);
		a.setLength(set2.size());
		a.setCount(count);
		a.getAddNumList().addAll(set0);

		return a;
	}

}
