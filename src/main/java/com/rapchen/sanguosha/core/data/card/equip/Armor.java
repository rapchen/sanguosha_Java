package com.rapchen.sanguosha.core.data.card.equip;

/**
 * 防具牌
 * @author Chen Runwen
 * @time 2023/5/14 0:01
 */
public abstract class Armor extends EquipCard {
    public Armor(Suit suit, Point point, String name, String nameZh) {
        super(suit, point, name, nameZh);
        this.subType = SubType.EQUIP_ARMOR;
    }
}
