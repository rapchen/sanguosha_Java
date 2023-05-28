package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.trick.ImmediateTrickCard;
import com.rapchen.sanguosha.core.data.card.trick.TrickCard;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 黄月英
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class HuangYueying extends General {
    public HuangYueying() {
        super("HuangYueying", "黄月英", Gender.FEMALE, Nation.SHU, 3);
        skills.add(JiZhi.class);
        skills.add(QiCai.class);
    }

    // 集智: 每当你使用一张非延时锦囊牌时，你可以摸一张牌。
    public static class JiZhi extends TriggerSkill {
        public JiZhi() {
            super("JiZhi", "集智", new Timing[]{Timing.CARD_USING});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            if (!(use.card instanceof ImmediateTrickCard)) return;
            if (askForUse(owner)) {
                doLog();
                owner.drawCards(1);
            }
        }
    }

    // 奇才: 锁定技，你使用锦囊牌无距离限制。
    public static class QiCai extends TriggerSkill {
        public QiCai() {
            super("QiCai", "奇才", new Timing[]{Timing.MD_DISTANCE_LIMIT});
            compulsory = true;
        }

        @Override
        public int onModify(Event event, int value) {
            Card card = (Card) event.xFields.get("Card");
            if (card instanceof TrickCard)
                return 10000;
            return value;
        }
    }

}
