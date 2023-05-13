package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 锦囊牌
 * @author Chen Runwen
 * @time 2023/5/5 18:43
 */
public abstract class TrickCard extends Card {
    public TrickCard(Suit suit, Point point) {
        super(suit, point);
    }

    /**
     * 询问无懈可击。非延时类是使用时对每个目标询问；延时类是即将生效时才询问
     * @param effect 卡牌效果
     * @return 是否有人无懈
     */
    public boolean askForNullification(CardEffect effect) {
        // TODO 应该是同时询问。以及现在如果第一个人用的无懈被无懈了，还会问第二个人
        for (Player player : Engine.eg.getAllPlayers()) {
            if (player.askForNullification(effect)) {
                return true;
            }
        }
        return false;
    }
}
