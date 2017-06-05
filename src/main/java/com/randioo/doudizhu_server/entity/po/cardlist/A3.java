package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.util.CardTools;

public class A3 extends A1 {
	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();

		return getNum() - ((A3) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 3)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(2);
		if (set.size() == 0)
			throw new CardListPatternException();

		int num = set.iterator().next();

		A3 a = new A3();
		a.setNum(num);
		return a;
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {
		if (arr.size() < 3 || cardSort.getCardSort().get(2).size() < 1)
			return;

		cardSort = cardSort.clone();
		if (lastCardList == null) {
			// 主动出牌
			for (int i = cardSort.getCardSort().size() - 1; i >= 2; i--) {
				Set<Integer> set = cardSort.getCardSort().get(i);
				List<List<Integer>> lists = new ArrayList<>();
				for (int pai : set) {
					List<Integer> list = new ArrayList<>(3);
					for (int j = 0; j < 2; j++)
						list.add(pai);
					lists.add(list);
				}
				List<Integer> temp = new ArrayList<>(set);
				for (int pai : temp) {
					CardTools.rmValue(cardSort, pai, i + 1);
				}
				recommandList.addAll(0, lists);
			}
		} else {
			// 被动出牌
			if (lastCardList.getClass() == A3.class) {
				A3 a1 = (A3) lastCardList;
				int num = a1.getNum();

				for (int i = cardSort.getCardSort().size() - 1; i >= 2; i--) {
					Set<Integer> set = cardSort.getCardSort().get(i);
					List<List<Integer>> lists = new ArrayList<>();
					for (int pai = num + 1; pai <= CardTools.C_2; pai++) {
						if (set.contains(pai)) {
							List<Integer> list = new ArrayList<>(3);
							for (int j = 0; j < 2; j++)
								list.add(pai);
							lists.add(list);
						}
					}
					List<Integer> temp = new ArrayList<>(set);
					for (int pai : temp) {
						CardTools.rmValue(cardSort, pai, i + 1);
					}
					recommandList.addAll(0, lists);
				}
			}
		}
	}

}
