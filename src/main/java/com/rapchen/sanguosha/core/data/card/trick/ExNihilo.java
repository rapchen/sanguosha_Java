package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 无中生有
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class ExNihilo extends ImmediateTrickCard {

    public ExNihilo(Card.Suit suit, Card.Point point) {
        super(suit, point);
        name = "ExNihilo";
        nameZh = "无中生有";
        benefit = 100;
    }

    @Override
    public boolean canUseTo(Player source, Player target) {
        return target == source;
    }

    @Override
    public void doEffect(Player source, Player target) {
        target.drawCards(2);
    }

}
