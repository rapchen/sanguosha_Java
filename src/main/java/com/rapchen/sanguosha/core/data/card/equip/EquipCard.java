package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 装备牌
 * @author Chen Runwen
 * @time 2023/5/5 18:43
 */
public abstract class EquipCard extends Card {
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
    }
}
