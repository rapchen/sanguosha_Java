package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 诸葛亮
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class ZhugeLiang extends General {
    public ZhugeLiang() {
        super("ZhugeLiang", "诸葛亮", Gender.MALE, Nation.SHU, 3);
        skills.add(GuanXing.class);
    }

    // TODO 观星 空城
    public static class GuanXing extends TriggerSkill {
        public GuanXing() {
            super("GuanXing", "观星", new Timing[]{Timing.PHASE_BEGIN});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            if (askForUse(owner)) {
                doLog();
                owner.drawCards(1);
            }
        }
    }

}
