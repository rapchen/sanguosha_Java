package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 张飞
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class ZhangFei extends General {
    public ZhangFei() {
        super("ZhangFei", "张飞", Gender.MALE, Nation.SHU, 4);
        skills.add(PaoXiao.class);
    }

    // 咆哮：锁定技，你使用【杀】无次数限制。
    public static class PaoXiao extends TriggerSkill {
        public PaoXiao() {
            super("PaoXiao", "咆哮", new Timing[]{Timing.MD_SLASH_LIMIT});
        }

        @Override
        public int onModify(Event event, int value) {
            return 1000000;  // 无限制
        }
    }
}
