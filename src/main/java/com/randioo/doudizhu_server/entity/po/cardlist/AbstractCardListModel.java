package com.randioo.doudizhu_server.entity.po.cardlist;

import java.util.Comparator;

import com.randioo.doudizhu_server.entity.po.CardSort;

public abstract class AbstractCardListModel implements Comparator<CardList> {

	enum CardTypeEnum {
		KQ, // 王炸
		A4, // 炸弹
		A1, // 单
		A2, // 对
		A3, // 三
		A3N1, // 三带一
		A3N2, // 三带二
		ABCDE, // 单顺
		A2B2C2, // 姐妹
		A3B3, // 三顺
		A3B3CD, // 飞机
		A4BC, // 四带二
	}

	public abstract CardTypeEnum getCardTypeEnum();

	public abstract CardList pattern(CardSort cardSort);

}
