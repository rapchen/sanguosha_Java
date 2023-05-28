package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 曹操
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class CaoCao extends General {
    public CaoCao() {
        super("CaoCao", "曹操", Gender.MALE, Nation.WEI, 4);
        skills.add(JianXiong.class);
    }

    // 奸雄: 每当你受到伤害后，你可以获得对你造成伤害的牌。
    public static class JianXiong extends TriggerSkill {
        public JianXiong() {
            super("JianXiong", "奸雄", new Timing[]{Timing.DAMAGED_DONE});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final Damage damage = (Damage) event.xFields.get("Damage");
            if (damage.effect == null || damage.effect.getCard() == null) return;
            Card card = damage.effect.getCard();

            if (askForUse(owner)) {
                Engine.eg.moveCard(card, Card.Place.HAND, owner, name);
                doLog(String.format("获得了 %s", card));
            }
        }
    }
}
