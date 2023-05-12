package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 南蛮入侵
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class BarbarianInvasion extends ImmediateTrickCard {
    public BarbarianInvasion(Card.Suit suit, Card.Point point) {
        super(suit, point);
        name = "BarbarianInvasion";
        nameZh = "南蛮入侵";
    }

    @Override
    public void doEffect(Player source, Player target) {
        if (!target.askForSlash()) {
            source.doDamage(target, 1);
        }
    }

}
