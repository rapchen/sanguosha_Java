package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 进攻马（-1马）
 * @author Chen Runwen
 * @time 2023/5/12 15:59
 */
public class OffensiveHorse extends EquipCard {
    public OffensiveHorse(Suit suit, Point point, String name, String nameZh) {
        super(suit, point, name, nameZh);
        this.subType = SubType.EQUIP_HORSE_OFF;
        this.skill = new OffensiveHorseSkill();
    }

    private static class OffensiveHorseSkill extends TriggerSkill {
        public OffensiveHorseSkill() {
            super("OffensiveHorseSkill", "进攻马", new Timing[]{Timing.MD_DISTANCE});
            compulsory = true;
        }

        @Override
        public int onModify(Event event, int value) {
            return value - 1;
        }
    }
}
