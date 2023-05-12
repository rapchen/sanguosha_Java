package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.player.Player;

/**
 * 万箭齐发
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class ArchersAttack extends ImmediateTrickCard {
    public ArchersAttack(Suit suit, Point point) {
        super(suit, point);
        name = "ArchersAttack";
        nameZh = "万箭齐发";
    }

    @Override
    public void doEffect(Player source, Player target) {
        if (!target.askForDodge(false)) {
            source.doDamage(target, 1);
        }
    }

}
