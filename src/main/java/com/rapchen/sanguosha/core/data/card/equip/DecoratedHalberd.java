package com.rapchen.sanguosha.core.data.card.equip;

/**
 * 方天画戟 DecoratedHalberd
 * @author Chen Runwen
 * @time 2023/5/14 0:18
 */
public class DecoratedHalberd extends Weapon {
    public DecoratedHalberd(Suit suit, Point point) {
        super(suit, point, "DecoratedHalberd", "方天画戟", 4);
    }

    // TODO 锁定技，若你使用的【杀】是你最后的手牌，则此【杀】可以多选择两个目标。
}
