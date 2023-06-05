package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 仁王盾
 * @author Chen Runwen
 * @time 2023/5/14 1:48
 */
public class RenWangShield extends Armor {
    public RenWangShield(Suit suit, Point point) {
        super(suit, point, "RenWangShield", "仁王盾");
        skill = new RenWangShieldSkill();
    }

    // 锁定技，黑色【杀】对你无效。
    private static class RenWangShieldSkill extends TriggerSkill {
        public RenWangShieldSkill() {
            super("RenWangShieldSkill", "仁王盾", new Timing[]{Timing.EFFECT_BEFORE});
            compulsory = true;
        }

        @Override
        public void onTrigger(Event event) {
            final CardEffect effect = (CardEffect) event.xFields.get("CardEffect");
            Card card = effect.getCard();
            if (card instanceof Slash && card.isBlack()) {
                doLog("%s 无效", card);
                effect.canceled = true;
            }
        }

        @Override
        public boolean canTrigger(Event event) {
            return super.canTrigger(event) && owner.equips.isArmorValid();
        }
    }
}
