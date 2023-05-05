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
    protected boolean askForPlayCard() {
        if (handCards.isEmpty()) return false;
        Card card = handCards.get(0);
        useCard(card);
        // TODO 选择了牌还需要选目标
        return card != null;
    }

    @Override
    protected void askForDiscard(int count) {
        for (int i = 0; i < count; i++) {
            discard(handCards.get(0));
        }
    }

    @Override
    protected Card askForChooseCard(List<Card> cards, String prompt, boolean forced) {
        return cards.get(0);
    }

    @Override
    protected int askForNumber(int max, boolean forced) {
        return 1;
    }

    @Override
    public boolean askForJink() {
        // TODO
        return false;
    }

}
