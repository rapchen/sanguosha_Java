package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.CardEffect;

/**
 * TODO 借刀杀人 （没有刀）
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class BorrowingKnife extends ImmediateTrickCard {
    public BorrowingKnife(Suit suit, Point point) {
        super(suit, point);
        name = "BorrowingKnife";
        nameZh = "借刀杀人";
    }

    @Override
    public void doEffect(CardEffect effect) {
        return;
    }

}
