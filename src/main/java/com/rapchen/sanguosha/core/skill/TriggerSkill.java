package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

/**
 * 触发技（包括修改类的技能）
 * @author Chen Runwen
 * @time 2023/5/14 12:14
 */
public abstract class TriggerSkill extends Skill {

    public Timing[] timings;
    public boolean onlyOwner = true;  // 是否只有技能拥有者的对应事件可以触发这个技能。默认是
    public TriggeredTransformSkill transSkill = null;  // 该触发技绑定的转化技。

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
     * 触发修正事件（如攻击范围修改、距离修改）时，进行修改，返回修正后的值
     * @param event 事件
     * @param value 修改前的值
     * @return 修改后的值
     */
    public int onModify(Event event, int value) {
        return value;
    }

    /**
     * 是否可触发。默认只包含判断onlyOwner的逻辑
     * @param event 当前的触发事件，包含时机、角色等信息
     */
    public boolean canTrigger(Event event) {
        if (onlyOwner) return event.player == owner;
        return true;
    }

    /* =============== end 子类重写的方法 =============== */
    /* =============== begin 工具方法 =============== */

    public void setTransSkill(TriggeredTransformSkill transSkill) {
        transSkill.triggerSkill = this;
        transSkill.nameZh = nameZh;

        this.transSkill = transSkill;
        subSkills.add(transSkill);
    }

    /**
     * 要求技能拥有者使用本触发技绑定的转化技
     * @param event 当前触发本触发技的事件
     * @return 是否使用
     */
    public final boolean askForTransform(Event event) {
        if (transSkill == null) return false;
        // 1. 选牌
        Card card = transSkill.askForTransform(new CardAsk(event, owner));
        if (card == null) return false;
        // 2. 选择目标
        List<Player> targets = owner.chooseTargets(card);
        if (targets == null) return false;
        // 3. 使用
        owner.useCard(card, targets);
        return true;
    }
    public final boolean askForTransform() {
        return askForTransform(null);
    }

}
