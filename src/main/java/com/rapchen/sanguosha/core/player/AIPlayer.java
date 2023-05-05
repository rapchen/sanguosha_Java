package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;

import java.util.List;

/**
 * @author Chen Runwen
 * @time 2023/4/24 18:10
 */
public class AIPlayer extends Player {
    public AIPlayer(Engine engine, int id, String name) {
        super(engine, id, name);
    }

    @Override
    protected Card choosePlayCard(List<Card> cards) {
        if (cards.isEmpty()) return null;
        return cards.get(0);
    }

    @Override
    protected Card chooseDiscard() {
        if (handCards.isEmpty()) return null;
        return handCards.get(0);
    }

    @Override
    protected Card chooseCard(List<Card> cards, String prompt, boolean forced) {
        if (cards.isEmpty() && !forced) return null;
        return cards.get(0);  // TODO 非forced情况可以考虑放弃
    }

    @Override
    protected int chooseNumber(int max, boolean forced) {
        return 1;
    }

}
