package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Skill;

/**
 * 装备牌
 * @author Chen Runwen
 * @time 2023/5/5 18:43
 */
public abstract class EquipCard extends Card {

    public Skill skill;

    public EquipCard(Suit suit, Point point, String name, String nameZh) {
        super(suit, point);
        this.name = name;
        this.nameZh = nameZh;
        throwAfterUse = false;  // 不进入弃牌堆
    }

    @Override
    public boolean canUseTo(Player source, Player target) {
        return target == source;
    }

    @Override
    public void doEffect(Player source, Player target) {
        // 装备统一处理：原来对应装备区的装备置入弃牌堆，新的置入对应装备区
        target.equips.useEquip(this);
        if (skill != null) {
            skill.owner = target;  // 武器技能拥有者为武器拥有者
            source.engine.skills.add(skill);
        }
    }

    /**
     * 失去装备的时机。这里需要处理装备技能的失去
     */
    public void onRemove() {
        if (skill != null) {
            Engine.eg.skills.remove(skill);
        }
    }
}
