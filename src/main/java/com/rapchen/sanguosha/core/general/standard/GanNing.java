package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.trick.Dismantlement;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.TransformSkill;

/**
 * 甘宁
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class GanNing extends General {
    public GanNing() {
        super("GanNing", "甘宁", Gender.MALE, Nation.WU, 4);
        skills.add(QiXi.class);
    }

    // 奇袭: 你可以将一张黑色牌当【过河拆桥】使用。
    public static class QiXi extends TransformSkill {
        public QiXi() {
            super("QiXi", "奇袭");
            maxCardCount = 1;
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.isBlack();
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() == 1) {
                return Card.createVirtualCard(Dismantlement.class, chosenCards);
            }
            return null;
        }

        @Override
        public boolean usableInPlayPhase() {
            return Card.validInPlayPhase(Dismantlement.class, owner);
        }
    }
}
