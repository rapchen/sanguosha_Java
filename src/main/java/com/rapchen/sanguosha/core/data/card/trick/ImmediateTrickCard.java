package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;

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
        // 询问无懈可击 TODO 应该是同时询问。以及现在如果第一个人用的无懈被无懈了，还会问第二个人
        for (Player player : effect.getSource().engine.getAllPlayers()) {
            if (player.askForNullification(effect)) {
                return true;
            }
        }
        return false;
    }
}
