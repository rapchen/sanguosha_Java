package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;

/**
 * 非延时类锦囊
 * @author Chen Runwen
 * @time 2023/5/5 18:43
 */
public abstract class ImmediateTrickCard extends TrickCard {
    public ImmediateTrickCard(Card.Suit suit, Card.Point point) {
        super(suit, point);
        subType = Card.SubType.TRICK_IMMEDIATE;
    }

    @Override
    public boolean checkCanceled(CardEffect effect) {
        // 非延时类锦囊，生效前直接询问无懈可击
        return askForNullification(effect);
    }

}
