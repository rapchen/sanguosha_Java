package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.TransformSkill;

/**
 * 刘备 SHU001
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class LiuBei extends General {
    public LiuBei() {
        super("LiuBei", "刘备", Gender.MALE, Nation.SHU, 4);
        skills.add(RenDe.class);
    }

    // TODO 仁德
    public static class RenDe extends TransformSkill {
        public RenDe() {
            super("RenDe", "仁德");
            maxCardCount = 10000;
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.place == Card.Place.HAND;  // 手牌
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() > 0) {
                // 仁德卡
                return Card.createVirtualCard(Slash.class, chosenCards);
            }
            return null;
        }
    }
}
