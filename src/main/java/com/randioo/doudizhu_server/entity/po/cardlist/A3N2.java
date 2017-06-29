package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.util.CardTools;

public class A3N2 extends A3N1 {

	@Override
	public int compareTo(CardList o) {
		if (o.getClass() == A4.class || o.getClass() == KQ.class)
			return -1;
		if (o.getClass() != getClass())
			throw new CardTypeComparableException();

		return getNum() - ((A3N2) o).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 5)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(1);
		Set<Integer> set2 = cardSort.getCardSort().get(2);
		if (set.size() == 2 && set2.size() == 1) {
			A3N2 a = new A3N2();
			a.setNum(set2.iterator().next());
			for (int i : set)
				if (i != a.getNum())
					a.setAddNum(i);

			return a;
		}

		throw new CardListPatternException();
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		if (lastCardList == null) {
			return;
		}
		if (arr.size() < 5 || cardSort.getCardSort().get(2).size() < 1 || cardSort.getCardSort().get(1).size() < 2)
			return;
		if (lastCardList.getClass() != getClass()) {
			return;
		}
		A3N2 a1 = (A3N2) lastCardList;
		int tnum = a1.getNum();

		List<List<Integer>> lists = new ArrayList<>();
		Set<Integer> tset = null;
		CardSort tcardSort = cardSort.clone();
		
		Set<Integer> set = tcardSort.getCardSort().get(3);				
		List<Integer> temp = new ArrayList<>(set);
		CardTools.rmAllValues(tcardSort, temp);

		Set<Integer> set2 = tcardSort.getCardSort().get(2);				
		temp = new ArrayList<>(set2);
		for (int value : temp) {
			tcardSort.getCardSort().get(1).remove(value);
		}
			
		tset = tcardSort.getCardSort().get(2);
		Set<Integer> set1 = tcardSort.getCardSort().get(1);
		if(!set1.iterator().hasNext())
			return;
		System.out.println("tset:"+tset);	
		for(int pai : tset){
			if(pai > tnum){
				List<Integer> list = new ArrayList<>(4);
				for (int k = 0; k < 3; k++)
					list.add(pai);
				int extra = set1.iterator().next();
				for (int k = 0; k < 2; k++)
					list.add(extra);
				lists.add(list);
			}
		}
		recommandList.addAll(recommandList.size(), lists);

	}

}
