package com.randioo.doudizhu_server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.entity.po.cardlist.A1;
import com.randioo.doudizhu_server.entity.po.cardlist.A2;
import com.randioo.doudizhu_server.entity.po.cardlist.A2B2C2;
import com.randioo.doudizhu_server.entity.po.cardlist.A3;
import com.randioo.doudizhu_server.entity.po.cardlist.A3B3;
import com.randioo.doudizhu_server.entity.po.cardlist.A3B3CD;
import com.randioo.doudizhu_server.entity.po.cardlist.A3N1;
import com.randioo.doudizhu_server.entity.po.cardlist.A3N2;
import com.randioo.doudizhu_server.entity.po.cardlist.A4;
import com.randioo.doudizhu_server.entity.po.cardlist.A4BC;
import com.randioo.doudizhu_server.entity.po.cardlist.ABCDE;
import com.randioo.doudizhu_server.entity.po.cardlist.CardList;
import com.randioo.doudizhu_server.entity.po.cardlist.KQ;
import com.randioo.doudizhu_server.error.CardListPatternException;

public class GameLogic {
	int[] pai = { 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, // 黑桃
			// 3 4 5 6 7 8 9 10 J Q K A 2
			0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, // 红桃

			0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D, // 草花

			0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, // 方片

			0x0E, 0x0F
			// Joker
	};

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

	public static void main(String[] args) {
		System.out.println(13 * 4 + 2);
		int[] pai = { 0x31, 0x31, 0x31, 0x31, 0x32, 0x34 };
		CardSort cardSort = fillCardSort(pai);
		System.out.println(cardSort);

		List<CardList> cardLists = new ArrayList<>(12);
		cardLists.add(new A1());
		cardLists.add(new A2());
		cardLists.add(new A3());
		cardLists.add(new A4());
		cardLists.add(new A3N1());
		cardLists.add(new A3N2());
		cardLists.add(new ABCDE());
		cardLists.add(new A2B2C2());
		cardLists.add(new A3B3());
		cardLists.add(new A3B3CD());
		cardLists.add(new A4BC());
		cardLists.add(new KQ());

		CardList result = identifyCardList(cardLists, cardSort, pai);

		System.out.println(result.getClass());
	}

	public static CardSort fillCardSort(int[] pais) {
		CardSort cardSort = new CardSort();
		for (int pai : pais) {
			int num = toNum(pai);
			for (Set<Integer> set : cardSort.getCardSort()) {
				if (!set.contains(num)) {
					set.add(num);
					break;
				}
			}
		}
		return cardSort;
	}

	private static CardList identifyCardList(List<CardList> cardLists, CardSort cardSort, int[] pai) {
		for (CardList cardList : cardLists) {
			try {
				return null;
			} catch (Exception e) {

			}
		}
		return null;
	}

}
