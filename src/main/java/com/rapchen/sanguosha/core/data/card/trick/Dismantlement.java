package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 过河拆桥
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class Dismantlement extends ImmediateTrickCard {
    public Dismantlement(Card.Suit suit, Card.Point point) {
        super(suit, point);
        name = "Dismantlement";
        nameZh = "过河拆桥";
    }

    @Override
    public void doUseToOne(Player source, Player target) {
        // TODO 实际是source要选牌
        target.askForDiscard(1);
    }

}
