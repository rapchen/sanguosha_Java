package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

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
    public List<Player> getFixedTargets(Player source) {
        return source.getOtherPlayers();
    }

    @Override
    public void doEffect(CardEffect effect) {
        Player source = effect.getSource(), target = effect.target;
        if (!target.askForSlash()) {
            source.doDamage(new Damage(effect));
        }
    }

}
