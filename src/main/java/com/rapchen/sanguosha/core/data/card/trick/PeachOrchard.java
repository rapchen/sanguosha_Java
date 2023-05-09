package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.player.Player;

/**
 * 桃园结义
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class PeachOrchard extends ImmediateTrickCard {
    public PeachOrchard(Suit suit, Point point) {
        super(suit, point);
        name = "PeachOrchard";
        nameZh = "桃园结义";
    }

    @Override
    public void doUseToOne(Player source, Player target) {
        target.doRecover(1);
    }

}
