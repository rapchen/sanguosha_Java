package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.FakeCard;
import com.rapchen.sanguosha.core.player.Player;

import java.util.*;

/**
 * 技能管理器
 * @author Chen Runwen
 * @time 2023/5/15 22:05
 */
public class SkillManager {
    public Set<Skill> skills;  // 所有技能列表，暂时没用
    public Map<Timing, List<TriggerSkill>> triggerSkills;  // 时机-触发技Map
    public Map<Player, List<TransformSkill>> transformSkills;  // 角色-转化技Map

    public SkillManager() {
        skills = new HashSet<>();
        triggerSkills = new HashMap<>();
        transformSkills = new HashMap<>();
    }

    /**
     * 添加技能。需要事先指定技能拥有者
     */
    public void add(Skill skill) {
        skills.add(skill);
        if (skill.owner != null) {
            skill.owner.skills.add(skill);
        }
        if (skill instanceof TriggerSkill tSkill) {
            addTriggerSkill(tSkill);
        } else if (skill instanceof TransformSkill tSkill) {
            if (!transformSkills.containsKey(tSkill.owner)) {
                transformSkills.put(tSkill.owner, new ArrayList<>());
            }
            transformSkills.get(tSkill.owner).add(tSkill);
        }
    }

    /**
     * 移除技能。不能在此之前先置空技能拥有者
     */
    public void remove(Skill skill) {
        skills.remove(skill);
        if (skill.owner != null) {
            skill.owner.skills.remove(skill);
        }
        if (skill instanceof TriggerSkill tSkill) {
            removeTriggerSkill(tSkill);
        } else if (skill instanceof TransformSkill tSkill) {
            List<TransformSkill> tSkills = transformSkills.get(tSkill.owner);
            if (tSkills != null) {
                tSkills.remove(skill);
            }
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

    /**
     * 触发一个修正（如攻击范围修改、距离修改）。以事件形式发送，返回修改后的值
     * @param event 事件
     * @param value 修改前的值
     */
    public int triggerModify(Event event, int value) {
        List<TriggerSkill> skills = triggerSkills.get(event.timing);
        if (skills == null) return value;
        for (TriggerSkill skill : skills) {
            if (skill.canTrigger(event)) {
                value = skill.onModify(event, value);
            }
        }
        return value;
    }

    /**
     * 根据用户当前可用的转化技列表返回虚拟卡牌
     * @param ask 当前CardAsk对象
     * @return 虚拟卡牌列表
     */
    public List<Card> getTransformedCards(CardAsk ask) {
        List<TransformSkill> saSkills = transformSkills.get(ask.player);
        if (saSkills == null) return new ArrayList<>();
        List<Card> res = new ArrayList<>();
        for (TransformSkill skill : saSkills) {
            if (ask.bannedSkills.contains(skill)) continue;  // 已经尝试过的技能
            boolean usable = ask.scene == CardAsk.Scene.PLAY ?
                    skill.usableInPlayPhase() : skill.usableAtResponse(ask);
            if (usable) {
                Card card = new FakeCard(skill.nameZh);
                card.setSkill(skill);
                res.add(card);  // 返回一个虚拟卡牌代表技能选项，不是转化后的卡牌
            }
        }
        return res;
    }

}
