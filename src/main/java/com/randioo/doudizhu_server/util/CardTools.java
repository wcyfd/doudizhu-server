package com.randioo.doudizhu_server.util;

import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;

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

	public static int[] toNums(int[] pai) {
		int[] temp = new int[pai.length];
		int i = 0;
		for (int t : pai) {
			temp[i++] = toNum(t);
		}
		return temp;
	}

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

}
