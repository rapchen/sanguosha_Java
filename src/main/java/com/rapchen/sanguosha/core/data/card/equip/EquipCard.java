package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Skill;

import java.util.List;

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
        willThrow = false;  // 不进入弃牌堆
    }

    @Override
    public List<Player> getFixedTargets(Player source) {
        return List.of(source);
    }

    @Override
    public void doEffect(CardEffect effect) {
        // 装备统一处理：原来对应装备区的装备置入弃牌堆，新的置入对应装备区
        Player target = effect.target;
        Engine.eg.moveCard(this, target.EQUIP, "useEquip");
        if (skill != null) {
            Engine.eg.skills.add(skill, target);
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
