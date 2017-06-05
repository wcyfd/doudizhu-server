package com.randioo.doudizhu_server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.entity.po.cardlist.A1;
import com.randioo.doudizhu_server.entity.po.cardlist.A3;
import com.randioo.doudizhu_server.entity.po.cardlist.ABCDE;
import com.randioo.doudizhu_server.entity.po.cardlist.CardList;

public class CardTools {
	public static final int C_3 = 1;
	public static final int C_4 = 2;
	public static final int C_5 = 3;
	public static final int C_6 = 4;
	public static final int C_7 = 5;
	public static final int C_8 = 6;
	public static final int C_9 = 7;
	public static final int C_10 = 8;
	public static final int C_J = 9;
	public static final int C_Q = 10;
	public static final int C_K = 11;
	public static final int C_A = 12;
	public static final int C_2 = 13;
	public static final int C_QUEUE = 14;
	public static final int C_KING = 15;

	/**
	 * 去掉花色
	 * 
	 * @param pai
	 * @return
	 * @author wcy 2017年5月27日
	 */
	public static int toNum(int pai) {
		return pai & 0x0F;
	}

	public static void fillCardSort(CardSort cardSort, int pai) {
		int num = toNum(pai);
		for (Set<Integer> set : cardSort.getCardSort()) {
			if (!set.contains(num)) {
				set.add(num);
				break;
			}
		}
	}

	public static void fillCardSort(CardSort cardSort, List<Integer> pais) {
		for (int pai : pais)
			fillCardSort(cardSort, pai);
	}

	public static void rmValue(CardSort cardSort, int[] arr) {
		for (int value : arr) {
			for (int i = cardSort.getCardSort().size() - 1; i >= 0; i--) {
				Set<Integer> set = cardSort.getCardSort().get(i);
				if (set.contains(value)) {
					set.remove(value);
					break;
				}
			}
		}
	}

	public static void rmValue(CardSort cardSort, int value, int count) {
		int num = count;
		for (int i = cardSort.getCardSort().size() - 1; i >= 0; i--) {
			Set<Integer> set = cardSort.getCardSort().get(i);
			if (!set.contains(value))
				continue;
			if (num == 0)
				break;
			set.remove(value);
			num--;
		}
	}
	
	public static void recommandNumCommonTemplate(List<List<Integer>> recommandList,CardSort cardSort,CardList lastCardList,List<Integer> arr,int lineIndex){
		cardSort = cardSort.clone();
		if (lastCardList == null) {
			// 主动出牌
			for (int i = 2; i >= 2; i--) {
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
				A1 a1 = (A1) lastCardList;
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

	public static void recommandStartNumAndLenCommonTemplate(List<List<Integer>> recommandList, CardSort cardSort,
			CardList lastCardList, List<Integer> arr, int lineIndex, int loopAddCount) {
		if (lastCardList != null) {
			ABCDE abcde = (ABCDE) lastCardList;

			Set<Integer> set = cardSort.getCardSort().get(lineIndex);
			// 如果理论的最后一个值大于A则返回
			for (int startNum = abcde.getNum() + 1; /* 起始值 */(startNum < (startNum + abcde.getLength()))
					&& (startNum + abcde.getLength()) < CardTools.C_A; startNum++) {
				if (!set.contains(startNum))
					continue;

				NOT_HAVE: {
					List<Integer> list = new ArrayList<>();
					// 获得起始值
					for (int value = startNum; value < abcde.getNum() + abcde.getLength(); value++) {
						if (!set.contains(value))
							break NOT_HAVE;

						for (int loop = 0; loop < loopAddCount; loop++)
							list.add(value);
					}
					recommandList.add(list);
				}
			}

		}
	}

}
