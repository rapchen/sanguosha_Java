package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

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
        benefit = 100;
    }

    @Override
    public List<Player> getFixedTargets(Player source) {
        return Engine.eg.getAllPlayers();
    }

    @Override
    public void doEffect(CardEffect effect) {
        effect.target.doRecover(1);
    }

}
