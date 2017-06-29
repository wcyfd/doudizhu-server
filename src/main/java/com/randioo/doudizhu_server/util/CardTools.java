package com.randioo.doudizhu_server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.entity.po.cardlist.A1;
import com.randioo.doudizhu_server.entity.po.cardlist.A3;
import com.randioo.doudizhu_server.entity.po.cardlist.A4;
import com.randioo.doudizhu_server.entity.po.cardlist.ABCDE;
import com.randioo.doudizhu_server.entity.po.cardlist.CardList;
import com.randioo.doudizhu_server.entity.po.cardlist.KQ;

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

	public static void rmValues(CardSort cardSort, List<Integer> arr) {
		for (int value : arr) {
			rmValue(cardSort, value);
		}
	}

	public static void rmValue(CardSort cardSort, int value) {
		for (int i = cardSort.getCardSort().size() - 1; i >= 0; i--) {
			Set<Integer> set = cardSort.getCardSort().get(i);
			if (set.contains(value)) {
				set.remove(value);
			}
		}
	}

	public static void rmAllValues(CardSort cardSort, List<Integer> values) {
		for (int value : values) {
			rmAllValue(cardSort, value);
		}
	}

	public static void rmAllValue(CardSort cardSort, int value) {
		for (int i = 0; i < cardSort.getCardSort().size(); i++) {
			Set<Integer> set = cardSort.getCardSort().get(i);
			set.remove(value);
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

	public static void recommandNumCommonTemplate(List<List<Integer>> recommandList, CardSort cardSort,
			CardList lastCardList, int lineIndex, Class<? extends A1> targetClass) {
		cardSort = cardSort.clone();
		// 从第三行往前查找，因为第四行表示炸弹，所以第四行不查
		if (lastCardList == null) {
			// 主动出牌
			List<List<Integer>> lists = new ArrayList<>();
			Set<Integer> tset = null;
			for(int i = 2 ; i >= lineIndex ; i-- ){
				CardSort tcardSort = cardSort.clone();
				for(int j = 2 ; j > lineIndex+(2-i) ; j-- ){
					
					Set<Integer> set = tcardSort.getCardSort().get(j);				
					List<Integer> temp = new ArrayList<>(set);
					CardTools.rmAllValues(tcardSort, temp);
				}
				
				tset = tcardSort.getCardSort().get(lineIndex);
				
				
				for(int pai : tset){
					List<Integer> list = new ArrayList<>(lineIndex + 1);
					for (int k = 0; k < lineIndex + 1; k++)
						list.add(pai);
					lists.add(list);
				}
				
				
				if(tset != null){
					//tcardSort = cardSort.clone();
					CardTools.rmAllValues(cardSort, new ArrayList<Integer>(tset));
				}
				
			}
			recommandList.addAll(recommandList.size(), lists);
			
			
		} else {
			// 被动出牌
			if (lastCardList.getClass() != targetClass) {
				return;
			}
			A1 a1 = (A1) lastCardList;
			int num = a1.getNum();

			List<List<Integer>> lists = new ArrayList<>();
			Set<Integer> tset = null;
			for(int i = 2 ; i >= lineIndex ; i-- ){
				CardSort tcardSort = cardSort.clone();
				for(int j = 2 ; j > lineIndex+(2-i) ; j-- ){
					
					Set<Integer> set = tcardSort.getCardSort().get(j);				
					List<Integer> temp = new ArrayList<>(set);
					CardTools.rmAllValues(tcardSort, temp);
				}
				
				tset = tcardSort.getCardSort().get(lineIndex);
				
				
				for(int pai : tset){
					if(pai > num){
						List<Integer> list = new ArrayList<>(lineIndex + 1);
						for (int k = 0; k < lineIndex + 1; k++)
							list.add(pai);
						lists.add(list);
					}
				}
				
				
				if(tset != null){
					//tcardSort = cardSort.clone();
					CardTools.rmAllValues(cardSort, new ArrayList<Integer>(tset));
				}
				
			}
			recommandList.addAll(recommandList.size(), lists);
		}
	}

	public static void recommandStartNumAndLenCommonTemplate(List<List<Integer>> recommandList, CardSort cardSort,
			CardList lastCardList, List<Integer> arr, int lineIndex, Class<? extends ABCDE> targetClass) {
		if (lastCardList != null) {
			if (lastCardList.getClass() != targetClass) {
				return;
			}
			ABCDE a1 =  (ABCDE) lastCardList;
			int num = a1.getNum();

			List<List<Integer>> lists = new ArrayList<>();
			Set<Integer> tset = null;

			CardSort tcardSort = cardSort.clone();
	
			Set<Integer> set = tcardSort.getCardSort().get(3);				
			List<Integer> temp = new ArrayList<>(set);
			CardTools.rmAllValues(tcardSort, temp);				
			tset = tcardSort.getCardSort().get(lineIndex);
				
			for(int pai : tset){
				if(pai > num){
					boolean flag = true;
					for(int count = 0 ; count < a1.getLength() ; count ++){
						if(!tset.contains(pai+count) || pai+count > C_A){
							flag = false;
							break;
						}
					}
					if(flag){						
						List<Integer> list = new ArrayList<>(lineIndex + 1);
						for(int count = 0 ; count < a1.getLength() ; count ++){
							for (int k = 0; k < lineIndex + 1; k++)
								list.add(pai+count);
						}
						lists.add(list);
					}
				}
			}	
			recommandList.addAll(recommandList.size(), lists);
		}
	}

}
