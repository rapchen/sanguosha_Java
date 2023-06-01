package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

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
    public List<Player> getFixedTargets(Player source) {
        return List.of(source);
    }

    @Override
    public void doEffect(CardEffect effect) {
        effect.target.drawCards(2);
    }

}
