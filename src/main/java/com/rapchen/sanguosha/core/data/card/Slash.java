package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

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

    @Override
    public void doUseToOne(Player source, Player target) {
        if (!target.askForJink()) {
            source.doDamage(target, 1);
        }
    }
}
