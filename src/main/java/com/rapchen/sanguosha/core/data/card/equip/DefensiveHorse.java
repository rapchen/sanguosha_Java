package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 防御马（+1马）
 * @author Chen Runwen
 * @time 2023/5/12 15:59
 */
public class DefensiveHorse extends EquipCard {
    public DefensiveHorse(Suit suit, Point point, String name, String nameZh) {
        super(suit, point, name, nameZh);
        this.subType = SubType.EQUIP_HORSE_DEF;
        this.skill = new DefensiveHorseSkill();
    }

    private static class DefensiveHorseSkill extends TriggerSkill {
        public DefensiveHorseSkill() {
            super("DefensiveHorseSkill", "防御马", new Timing[]{Timing.MD_DISTANCE});
            compulsory = true;
            onlyOwner = false;  // 其他角色判定距离时生效
        }

        @Override
        public int onModify(Event event, int value) {
            Player target = (Player) event.xField.get("Target");
            if (target == owner) {
                return value + 1;
            }
            return value;
        }
    }
}
