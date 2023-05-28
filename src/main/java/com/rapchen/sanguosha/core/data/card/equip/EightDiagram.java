package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.basic.Dodge;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 八卦阵
 * @author Chen Runwen
 * @time 2023/5/14 1:48
 */
public class EightDiagram extends Armor {
    public EightDiagram(Suit suit, Point point) {
        super(suit, point, "EightDiagram", "八卦阵");
        skill = new EightDiagramSkill();
    }

    // 当你使用【杀】指定一名异性角色为目标后，你可以令其选择一项：弃一张手牌；或令你摸一张牌。
    private static class EightDiagramSkill extends TriggerSkill {
        public EightDiagramSkill() {
            super("EightDiagramSkill", "八卦阵", new Timing[]{Timing.CARD_ASKED});
        }

        @Override
        public void onTrigger(Event event) {
            final CardAsk ask = (CardAsk) event.xFields.get("CardAsk");
            if (ask.contains(Dodge.class)) {
                if (askForUse(owner)) {
                    Judgement judge = owner.doJudge(nameZh, Card::isRed);
                    if (judge.success) {
                        Dodge dodge = new Dodge(Suit.SUIT_NO, Point.POINT_NO);
                        dodge.virtual = true;
                        owner.xFields.put("CardProvided", dodge);  // 提供虚拟闪
                    }
                }
            }
        }

        @Override
        public boolean canTrigger(Event event) {
            return super.canTrigger(event) && owner.equips.isArmorValid();
        }
    }
}
