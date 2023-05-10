package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.CardUseToOne;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 无懈可击
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class Nullification extends ImmediateTrickCard {

    public CardUseToOne targetUse = null;  // 这张无懈的目标牌

    public Nullification(Suit suit, Point point) {
        super(suit, point);
        name = "Nullification";
        nameZh = "无懈可击";
    }

    @Override
    public boolean canUseInPlayPhase(Player player) {
        return false;
    }

    @Override
    public void doUseToAll(CardUse use) {
        // 给无懈可击询问无懈。如果被无懈，则上一个Nullified标记
        Player target = targetUse == null ? null : targetUse.getSource();
        CardUseToOne useToOne = new CardUseToOne(use, target);
        if (checkCanceled(useToOne)) {
            xFields.put("Nullified", null);
        }
    }
}
