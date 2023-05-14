package com.rapchen.sanguosha.core.data.card.equip;

/**
 * 武器牌
 * @author Chen Runwen
 * @time 2023/5/14 0:01
 */
public abstract class Weapon extends EquipCard {

    public int range = 1;

    public Weapon(Suit suit, Point point, String name, String nameZh, int range) {
        super(suit, point, name, nameZh);
        this.range = range;
        this.subType = SubType.EQUIP_WEAPON;
    }
}
