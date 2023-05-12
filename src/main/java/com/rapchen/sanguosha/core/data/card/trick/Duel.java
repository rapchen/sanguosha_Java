package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.player.Player;

/**
 * 决斗
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class Duel extends ImmediateTrickCard {
    public Duel(Suit suit, Point point) {
        super(suit, point);
        name = "Duel";
        nameZh = "决斗";
    }

    @Override
    public void doEffect(Player source, Player target) {
        while (true) {
            if (!target.askForSlash()) {
                source.doDamage(target, 1);
                break;
            }
            if (!source.askForSlash()) {
                target.doDamage(source, 1);
                break;
            }
        }
    }

}
