package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;

public class A1Model extends AbstractCardListModel {
	
	@Override
	public CardTypeEnum getCardTypeEnum() {
		return CardTypeEnum.A1;
	}

	@Override
	public CardList pattern(CardSort cardSort) {

		Set<Integer> set1 = cardSort.getCardSort().get(0);
		Set<Integer> set2 = cardSort.getCardSort().get(1);
		if (set2.size() == 0 && set1.size() == 1) {
			A1 entity = new A1();
			entity.setModel(this);
			entity.setNum(set1.iterator().next());
			return entity;
		}
		throw new CardListPatternException();
	}

	@Override
	public int compare(CardList o1, CardList o2) {
		if (o2.model.getCardTypeEnum() == CardTypeEnum.A4 || o2.model.getCardTypeEnum() == CardTypeEnum.KQ)
			return -1;
		if (o2.model.getCardTypeEnum() != o1.model.getCardTypeEnum())
			throw new CardTypeComparableException();

		return ((A1) o1).getNum() - ((A1) o2).getNum();
	}
	

}
