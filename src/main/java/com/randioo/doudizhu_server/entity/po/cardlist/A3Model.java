package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;

public class A3Model extends A1Model {

	@Override
	public CardTypeEnum getCardTypeEnum() {
		return CardTypeEnum.A3;
	}

	@Override
	public CardList pattern(CardSort cardSort) {
		List<Set<Integer>> list = cardSort.getCardSort();
		Set<Integer> set1 = list.get(0);
		Set<Integer> set2 = list.get(1);
		Set<Integer> set3 = list.get(2);
		Set<Integer> set4 = list.get(3);
		if (set1.size() == 1 && set1.size() == set2.size() && set1.size() == set3.size() && set4.size() == 0) {
			int value = set1.iterator().next();
			if (value == set2.iterator().next() && value == set3.iterator().next()) {
				A3 entity = new A3();
				entity.setNum(value);
				entity.setModel(this);

				return entity;
			}
		}

		throw new CardListPatternException();
	}

}
