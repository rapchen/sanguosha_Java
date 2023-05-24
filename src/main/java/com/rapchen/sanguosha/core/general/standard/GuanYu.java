package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.TransformSkill;

/**
 * 关羽
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class GuanYu extends General {
    public GuanYu() {
        super("GuanYu", "关羽", Gender.MALE, Nation.SHU, 4);
        skills.add(WuSheng.class);
    }

    // 武圣：你可以将红色牌当【杀】使用或打出。
    public static class WuSheng extends TransformSkill {
        public WuSheng() {
            super("WuSheng", "武圣");
            maxCardCount = 1;
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.isRed();
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() == 1) {
                return Card.createVirtualCard(Slash.class, chosenCards);
            }
            return null;
        }

        @Override
        public boolean usableInPlayPhase() {
            return Card.validInPlayPhase(Slash.class, owner);
        }

        @Override
        public boolean usableAtResponse(CardAsk ask) {
            return ask.contains(Slash.class);
        }
    }
}
