package com.rapchen.sanguosha.core.skill;

/**
 * 闭月
 * @author Chen Runwen
 * @time 2023/5/14 16:05
 */
public class BiYue extends TriggerSkill {
    public BiYue() {
        super("BiYue", "闭月", new Timing[]{Timing.PHASE_BEGIN});
    }

    @Override
    public void onTrigger(Event event) {
        doLog();
        owner.drawCards(1);
    }

}
