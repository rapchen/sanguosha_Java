package com.rapchen.sanguosha.core.skill;

/**
 * 触发技
 * @author Chen Runwen
 * @time 2023/5/14 12:14
 */
public abstract class TriggerSkill extends Skill {

    public Timing[] timings;
    public boolean onlyOwner = true;  // 是否只有技能拥有者的对应事件可以触发这个技能。默认是

    public TriggerSkill(String name, String nameZh, Timing[] timings) {
        super(name, nameZh);
        this.timings = timings;
    }

    /**
     * 触发技能时需要执行的效果
     * @param event 当前的触发事件，包含时机、角色等信息
     */
    public void onTrigger(Event event) {}

    /**
     * 是否可触发。默认只包含判断onlyOwner的逻辑
     * @param event 当前的触发事件，包含时机、角色等信息
     */
    public boolean canTrigger(Event event) {
        if (onlyOwner) return event.player == owner;
        return true;
    }
}
