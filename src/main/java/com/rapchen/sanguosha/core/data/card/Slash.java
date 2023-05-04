package com.rapchen.sanguosha.core.data.card;

/**
 * @author Chen Runwen
 * @time 2023/4/24 22:23
 */
public class Slash extends Card {

    public Slash(Suit suit, Point point, int id) {
        super(suit, point, id);
        name = "slash";
        nameZh = "杀";
    }
}
