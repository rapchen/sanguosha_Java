package com.rapchen.sanguosha.core.data.card;

import java.util.ArrayList;

/**
 * 无真实意义的卡牌，一般用于绑定多个子卡
 * @author Chen Runwen
 * @time 2023/5/6 11:05
 */
public class FakeCard extends Card {

    public FakeCard(String nameZh) {
        this(Suit.SUIT_NO, Point.POINT_NO);
        this.nameZh = nameZh;
    }

    public FakeCard(Suit suit, Point point) {
        super(suit, point, -1);
        virtual = true;
        name = "Dummy";
        nameZh = "假卡";
        subType = SubType.SUBTYPE_NO;
        subCards = new ArrayList<>();
    }

}
