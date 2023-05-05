package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

/**
 * 杀
 * @author Chen Runwen
 * @time 2023/4/24 22:23
 */
public class Slash extends Card {

    public Slash(Suit suit, Point point) {
        super(suit, point);
        name = "slash";
        nameZh = "杀";
        subType = SubType.BASIC;
    }

    @Override
    public void doUseToOne(Player source, Player target) {
        if (!target.askForDodge()) {
            source.doDamage(target, 1);
        }
    }
}
