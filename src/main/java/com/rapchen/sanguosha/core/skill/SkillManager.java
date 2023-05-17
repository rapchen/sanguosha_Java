package com.rapchen.sanguosha.core.skill;

import java.util.*;

/**
 * 技能管理器
 * @author Chen Runwen
 * @time 2023/5/15 22:05
 */
public class SkillManager {
    public Set<Skill> skills;  // 所有技能列表，暂时没用
    public Map<Timing, List<TriggerSkill>> triggerSkills;  // 时机-触发技Map

    public SkillManager() {
        skills = new HashSet<>();
        triggerSkills = new HashMap<>();
    }

    public void add(Skill skill) {
        skills.add(skill);
        if (skill instanceof TriggerSkill tSkill) {
            addTriggerSkill(tSkill);
        }
    }

    public void remove(Skill skill) {
        skills.remove(skill);
        if (skill instanceof TriggerSkill tSkill) {
            removeTriggerSkill(tSkill);
        }
    }

    /**
     * 添加触发技到对应时机
     */
    public void addTriggerSkill(TriggerSkill skill) {
        for (Timing timing : skill.timings) {
            if (!triggerSkills.containsKey(timing)) {
                triggerSkills.put(timing, new ArrayList<>());
            }
            triggerSkills.get(timing).add(skill);
        }
    }

    private void removeTriggerSkill(TriggerSkill skill) {
        for (Timing timing : skill.timings) {
            List<TriggerSkill> tSkills = triggerSkills.get(timing);
            if (tSkills != null) {
                tSkills.remove(skill);
            }
        }
    }

    /**
     * 触发一个事件
     * @param event 事件
     */
    public void trigger(Event event) {
        List<TriggerSkill> skills = triggerSkills.get(event.timing);
        if (skills == null) return;
        for (TriggerSkill skill : skills) {
            if (skill.canTrigger(event)) {
                skill.onTrigger(event);
            }
        }
    }
}
