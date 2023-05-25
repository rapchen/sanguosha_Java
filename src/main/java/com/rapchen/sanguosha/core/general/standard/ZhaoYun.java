package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.basic.Dodge;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.TransformSkill;

/**
 * 赵云
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class ZhaoYun extends General {
    public ZhaoYun() {
        super("ZhaoYun", "赵云", Gender.MALE, Nation.SHU, 4);
        skills.add(LongDan.class);
    }

    // 龙胆: 你可以将一张【杀】当【闪】使用或打出，或将一张【闪】当普通【杀】使用或打出。
    public static class LongDan extends TransformSkill {
        public LongDan() {
            super("LongDan", "龙胆");
            maxCardCount = 1;
        }

        @Override
        public boolean cardFilter(Card card) {
            if (ask.contains(Dodge.class)) {
                return card instanceof Slash;
            } else {
                return card instanceof Dodge;
            }
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() == 1) {
                if (ask.contains(Dodge.class)) {
                    return Card.createVirtualCard(Dodge.class, chosenCards);
                } else {
                    return Card.createVirtualCard(Slash.class, chosenCards);
                }
            }
            return null;
        }

        @Override
        public boolean usableInPlayPhase() {
            return Card.validInPlayPhase(Slash.class, owner);  // 出牌阶段只能主动用杀
        }

        @Override
        public boolean usableAtResponse(CardAsk ask) {
            return ask.contains(Slash.class) || ask.contains(Dodge.class) ;
        }
    }
}
