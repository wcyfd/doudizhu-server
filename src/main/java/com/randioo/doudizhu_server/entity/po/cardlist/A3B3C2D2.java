package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;
import com.randioo.doudizhu_server.error.CardTypeComparableException;
import com.randioo.doudizhu_server.util.CardTools;

public class A3B3C2D2 extends A3B3 {

	private List<Integer> addNumList = new ArrayList<>();

	public List<Integer> getAddNumList() {
		return addNumList;
	}

	public void setAddNumList(List<Integer> addNumList) {
		this.addNumList = addNumList;
	}

	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();

		A3B3C2D2 a3b3c2d2 = (A3B3C2D2) arg0;
		if (getLength() != a3b3c2d2.getLength())
			throw new CardTypeComparableException();

		return getNum() - ((A3B3C2D2) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if ((arr.size() >= 10) && (arr.size() - 10) % 5 != 0)
			throw new CardListPatternException();

		Set<Integer> set2 = cardSort.getCardSort().get(2);
		if (set2.size() < 2)
			throw new CardListPatternException();
		cardSort = cardSort.clone();

		Set<Integer> set0 = cardSort.getCardSort().get(0);
		Set<Integer> set1 = cardSort.getCardSort().get(1);
		Set<Integer> set3 = cardSort.getCardSort().get(3);
		set2 = cardSort.getCardSort().get(2);

		int tmp = 0;
		int first = 0;
		int len = 0;
		for (int value : set2) {
			if (tmp == 0) {
				// 如果第三行数组中第一个数字是大于K则就没有可能了
				if (value > CardTools.C_K)
					throw new CardListPatternException();

				first = value;
				tmp = value;
			} else {
				if (value > CardTools.C_A)
					break;
			}
			len++;
		}
		if (len < 2)
			throw new CardListPatternException();

		for (int n = first; n < first+len; n++)
			CardTools.rmValue(cardSort, n, 3);



		if (set0.size()  == len && set1.size() == len) {
		} else {
			throw new CardListPatternException();
		}

		A3B3C2D2 a = new A3B3C2D2();
		a.setNum(first);
		a.setLength(len);
		a.getAddNumList().addAll(set0);
		a.getAddNumList().addAll(set1);

		return a;
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		if (arr.size() < 10){
			return;
		}
		if (lastCardList == null) {
			return;
		}
		if (lastCardList.getClass() != getClass()) {
			return;
		}
		A3B3C2D2 a1 =  (A3B3C2D2) lastCardList;
		int num = a1.getNum();
		List<List<Integer>> lists = new ArrayList<>();
		Set<Integer> tset = null;
		CardSort tcardSort = cardSort.clone();

		Set<Integer> set = tcardSort.getCardSort().get(3);				
		List<Integer> temp = new ArrayList<>(set);
		CardTools.rmAllValues(tcardSort, temp);	
		
		set = tcardSort.getCardSort().get(2);				
		temp = new ArrayList<>(set);
		for (int value : temp) {
			tcardSort.getCardSort().get(1).remove(value);
		}		
		List<Integer> extra = new ArrayList<>(tcardSort.getCardSort().get(1));
		if(extra.size() < a1.getLength()){
			return;
		}
		Collections.sort(extra);
		tset = tcardSort.getCardSort().get(2); 		
			
		for(int pai : tset){
			if(pai > num){
				boolean flag = true;
				for(int count = 0 ; count < a1.getLength() ; count ++){
					if(!tset.contains(pai+count) || pai+count > CardTools.C_A){
						flag = false;
						break;
					}
				}
				if(flag){						
					List<Integer> list = new ArrayList<>();
					for(int count = 0 ; count < a1.getLength() ; count ++){
						for (int k = 0; k < 3; k++)
							list.add(pai+count);
					}
					for(int count = 0 ; count < a1.getLength() ; count ++){
						for (int k = 0; k < 2; k++)
							list.add(extra.get(count));
					}
					lists.add(list);
				}
			}	
		}
		recommandList.addAll(recommandList.size(), lists);
	}


}
