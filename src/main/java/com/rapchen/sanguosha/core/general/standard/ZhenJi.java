package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.basic.Dodge;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TransformSkill;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 甄姬
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class ZhenJi extends General {
    public ZhenJi() {
        super("ZhenJi", "甄姬", Gender.FEMALE, Nation.WEI, 3);
        skills.add(QingGuo.class);
        skills.add(LuoShen.class);
    }

    // 倾国: 你可以将一张黑色手牌当【闪】使用或打出。
    public static class QingGuo extends TransformSkill {
        public QingGuo() {
            super("QingGuo", "倾国");
            maxCardCount = 1;
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.isBlack() && card.place.isHand();
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() == 1) {
                return Card.createVirtualCard(Dodge.class, chosenCards);
            }
            return null;
        }

        @Override
        public boolean usableInPlayPhase() {
            return false;
        }

        @Override
        public boolean usableAtResponse(CardAsk ask) {
            return ask.contains(Dodge.class) ;
        }
    }

    // 洛神: 准备阶段开始时，你可以进行判定：若结果为黑色，判定牌生效后你获得之，然后你可以再次发动“洛神”。
    public static class LuoShen extends TriggerSkill {
        public LuoShen() {
            super("LuoShen", "洛神", new Timing[]{Timing.PHASE_BEGIN});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            if (owner.phase != Phase.PHASE_PREPARE) return;
            while (true) {
                if (askForUse(owner)) {
                    Judgement judge = owner.doJudge(nameZh, Card::isBlack);
                    if (judge.success) {
                        doLog(String.format("获得了判定牌 %s", judge.card));
                        owner.obtain(judge.card, name);
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
