package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 诸葛连弩
 * @author Chen Runwen
 * @time 2023/5/14 0:08
 */
public class ZhugeCrossbow extends Weapon {
    public ZhugeCrossbow(Suit suit, Point point) {
        super(suit, point, "ZhugeCrossbow", "诸葛连弩", 1);
        skill = new ZhugeCrossbowSkill();
    }

    // 锁定技，你使用【杀】无次数限制。
    private static class ZhugeCrossbowSkill extends TriggerSkill {
        public ZhugeCrossbowSkill() {
            super("ZhugeCrossbowSkill", "诸葛连弩", new Timing[]{Timing.MD_SLASH_LIMIT});
        }

        @Override
        public int onModify(Event event, int value) {
            return 1000000;  // 无限制
        }
    }
}
