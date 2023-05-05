package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;

/**
 * 锦囊牌
 * @author Chen Runwen
 * @time 2023/5/5 18:43
 */
public abstract class TrickCard extends Card {
    public TrickCard(Suit suit, Point point) {
        super(suit, point);
    }
}
