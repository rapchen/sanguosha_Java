package com.rapchen.sanguosha.core.data.card.basic;

import com.rapchen.sanguosha.core.player.Player;

/**
 * 桃
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class Peach extends BasicCard {
    public Peach(Suit suit, Point point) {
        super(suit, point);
        name = "Peach";
        nameZh = "桃";
        benefit = 100;
    }

    @Override
    public boolean canUseInPlayPhase(Player player) {
        return player.hp < player.maxHp;
    }

    @Override
    public void doUseToOne(Player source, Player target) {
        target.doRecover(1);
    }

}
