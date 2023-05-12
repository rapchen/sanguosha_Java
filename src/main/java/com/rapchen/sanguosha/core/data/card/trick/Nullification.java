package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 无懈可击
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class Nullification extends ImmediateTrickCard {

    public CardEffect targetEffect = null;  // 这张无懈的目标卡牌效果

    public Nullification(Suit suit, Point point) {
        super(suit, point);
        name = "Nullification";
        nameZh = "无懈可击";
    }

    @Override
    public boolean validInPlayPhase(Player player) {
        return false;
    }

    @Override
    public void doUseToAll(CardUse use) {
        // 给无懈可击询问无懈。如果被无懈，则上一个Nullified标记
        Player target = targetEffect == null ? null : targetEffect.getSource();
        CardEffect effect = new CardEffect(use, target);
        if (askForNullification(effect)) {
            xFields.put("Nullified", null);
        }
    }
}
