package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 贯石斧 Stone Breaker
 * @author Chen Runwen
 * @time 2023/5/14 0:18
 */
public class StoneAxe extends Weapon {
    public StoneAxe(Suit suit, Point point) {
        super(suit, point, "StoneAxe", "贯石斧", 3);
        skill = new StoneAxeSkill();
    }

    private static class StoneAxeSkill extends TriggerSkill {
        public StoneAxeSkill() {
            super("StoneAxeSkill", "贯石斧", new Timing[]{Timing.SLASH_DODGED});
        }

        @Override
        public void onTrigger(Event event) {
            CardChoose choose = new CardChoose(owner)
                    .fromSelf("he").filter(card -> card != owner.equips.getWeapon())
                    .count(2).reason(name, "是否弃置2张牌使用贯石斧？");
            if (owner.askForDiscard(choose)) {
                doLog("此杀仍造成伤害");
                owner.xFields.put("Slash_Undodged", true);  // 打上标记，仍造成伤害
            }
        }
    }
}
