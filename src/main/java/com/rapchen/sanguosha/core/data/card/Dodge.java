package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

/**
 * 闪
 * @author Chen Runwen
 * @time 2023/4/24 22:23
 */
public class Dodge extends Card {

    public Dodge(Suit suit, Point point) {
        super(suit, point);
        name = "Dodge";
        nameZh = "闪";
        subType = SubType.BASIC;
    }

}
