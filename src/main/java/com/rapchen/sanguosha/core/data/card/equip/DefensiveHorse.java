package com.rapchen.sanguosha.core.data.card.equip;

/**
 * 防御马（+1马）
 * @author Chen Runwen
 * @time 2023/5/12 15:59
 */
public class DefensiveHorse extends EquipCard {
    public DefensiveHorse(Suit suit, Point point, String name, String nameZh) {
        super(suit, point, name, nameZh);
        this.subType = SubType.EQUIP_HORSE_DEF;
    }
}
