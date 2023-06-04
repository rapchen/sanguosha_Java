package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 貂蝉 TODO 离间
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class DiaoChan extends General {
    public DiaoChan() {
        super("DiaoChan", "貂蝉", Gender.FEMALE, Nation.QUN, 3);
        skills.add(BiYue.class);
        // TODO 离间
    }

    public static class BiYue extends TriggerSkill {
        public BiYue() {
            super("BiYue", "闭月", new Timing[]{Timing.PHASE_BEGIN});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final Phase phase = (Phase) event.xFields.get("Phase");
            if (phase != Phase.PHASE_END) return;
            if (askForUse(owner)) {
                doLog();
                owner.drawCards(1);
            }
        }
    }
}
