package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.util.CardTools;

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

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		if (arr.size() < 4 || cardSort.getCardSort().get(3).size() < 1)
			return;
		
		cardSort = cardSort.clone();
		if (lastCardList == null) {
			// 主动出牌
			List<List<Integer>> lists = new ArrayList<>();
			Set<Integer> set = cardSort.getCardSort().get(3);				
			for(int pai : set){
				List<Integer> list = new ArrayList<>(4);
				for (int k = 0; k < 4; k++)
					list.add(pai);
				lists.add(list);
			}
			recommandList.addAll(recommandList.size(), lists);
			
			
		} else {
			// 被动出牌
			if (lastCardList.getClass() == KQ.class) {
				return;
			}else{
				boolean bomb = (lastCardList.getClass() == A4.class);
				List<List<Integer>> lists = new ArrayList<>();
				Set<Integer> set = cardSort.getCardSort().get(3);				
				for(int pai : set){
					if(!( bomb && ((A4)lastCardList).getNum() > pai)){
						List<Integer> list = new ArrayList<>(4);
						for (int k = 0; k < 4; k++)
							list.add(pai);
						lists.add(list);
					}					
				}
				recommandList.addAll(recommandList.size(), lists);
			}
			
		}

	}

}
