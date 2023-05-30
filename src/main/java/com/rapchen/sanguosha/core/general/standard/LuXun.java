package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardMove;
import com.rapchen.sanguosha.core.data.card.trick.Indulgence;
import com.rapchen.sanguosha.core.data.card.trick.Snatch;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 陆逊
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class LuXun extends General {
    public LuXun() {
        super("LuXun", "陆逊", Gender.MALE, Nation.WEI, 3);
        skills.add(QianXun.class);
        skills.add(LianYing.class);
    }

    // 谦逊: 锁定技，你不能被选择为【顺手牵羊】与【乐不思蜀】的目标。
    public static class QianXun extends TriggerSkill {
        public QianXun() {
            super("QianXun", "谦逊", new Timing[]{Timing.MD_TARGET_VALIDATION});
            compulsory = true;
        }

        @Override
        public int onModify(Event event, int value) {
            final Card card = (Card) event.xFields.get("Card");
            if (card instanceof Snatch || card instanceof Indulgence) return 0;
            return value;
        }
    }

    // 连营: 每当你失去最后的手牌后，你可以摸一张牌。
    public static class LianYing extends TriggerSkill {
        public LianYing() {
            super("LianYing", "连营", new Timing[]{Timing.CARD_MOVED});
            onlyOwner = false;
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final CardMove move = (CardMove) event.xFields.get("CardMove");
            if (move.loseLastHandcardPlayers.contains(owner)) {
                if (askForUse(owner)) {
                    doLog();
                    owner.drawCards(1);
                }
            }
        }
    }
}
