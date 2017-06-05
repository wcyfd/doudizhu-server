package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.util.CardTools;
import com.randioo.randioo_server_base.template.Function;

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

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {
		if (arr.size() < 1)
			return;

		cardSort = cardSort.clone();
		if (lastCardList == null) {
			// 主动出牌
			for (int i = cardSort.getCardSort().size() - 1; i >= 0; i--) {
				Set<Integer> set = cardSort.getCardSort().get(i);
				List<List<Integer>> lists = new ArrayList<>();
				for (int pai : set) {
					List<Integer> list = new ArrayList<>(1);
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
			if (lastCardList.getClass() == A1.class) {
				A1 a1 = (A1) lastCardList;
				int num = a1.getNum();

				for (int i = cardSort.getCardSort().size() - 1; i >= 0; i--) {
					Set<Integer> set = cardSort.getCardSort().get(i);
					List<List<Integer>> lists = new ArrayList<>();
					for (int pai = num + 1; pai <= CardTools.C_KING; pai++) {
						if (set.contains(pai)) {
							List<Integer> list = new ArrayList<>(1);
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

	public static void main(String[] args) {
		A1 a1 = new A1();
		List<List<Integer>> recommandList = new ArrayList<>();
		List<Integer> list = new ArrayList<>();
		list.add(0x11);
		list.add(0x13);
		list.add(0x1B);
		list.add(0x16);
		list.add(0x16);
		list.add(0x0E);

		CardSort cardSort = new CardSort();
		CardTools.fillCardSort(cardSort, list);

		A1 lastCardList = new A1();
		lastCardList.setNum(3);
		a1.recommand(recommandList, cardSort, lastCardList, list);

		System.out.println(recommandList);
	}

}
