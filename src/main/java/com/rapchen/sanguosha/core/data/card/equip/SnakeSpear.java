package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.skill.ServeAsSkill;

/**
 * 丈八蛇矛 Zhangba Snake Spear
 * @author Chen Runwen
 * @time 2023/5/14 0:18
 */
public class SnakeSpear extends Weapon {
    public SnakeSpear(Suit suit, Point point) {
        super(suit, point, "SnakeSpear", "丈八蛇矛", 3);
        skill = new SnakeSpearSkill();
    }

    // 你可以将两张手牌当【杀】使用或打出。
    private static class SnakeSpearSkill extends ServeAsSkill {
        public SnakeSpearSkill() {
            super("SnakeSpearSkill", "丈八蛇矛");
            maxCardCount = 2;
        }

        @Override
        public boolean cardFilter(Card card) {
            return super.cardFilter(card);
        }

        @Override
        public Card serveAs() {
            return super.serveAs();
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