package com.rapchen.sanguosha.core.data.card.basic;

import com.rapchen.sanguosha.core.data.card.Card;

/**
 * 锦囊牌
 * @author Chen Runwen
 * @time 2023/5/5 18:43
 */
public abstract class BasicCard extends Card {
    public BasicCard(Suit suit, Point point) {
        super(suit, point);
        subType = SubType.BASIC;
    }
}
