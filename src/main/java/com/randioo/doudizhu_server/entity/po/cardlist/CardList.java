package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.List;

import com.randioo.doudizhu_server.entity.po.CardSort;
import com.randioo.doudizhu_server.error.CardListPatternException;

public abstract class CardList implements Comparable<CardList> {

	public abstract CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException;

}
