package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardUse;

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
    protected Card chooseDiscard(Player target, List<Card> cards) {
        if (cards.isEmpty()) return null;
        return cards.get(0);
    }

    @Override
    public Card chooseCard(List<Card> cards, boolean forced, String prompt, String reason) {
        if (cards.isEmpty()) return null;
        switch (reason) {
            case "askForNullification":  // TODO 加各种时机
                CardUse use = (CardUse) xFields.getOrDefault("askForNullification_CardUse", null);
                if (use == null) return null;
                if (use.card.good == (use.currentTarget != this))  // 如果对我有坏处，或者对别人有好处，就用无懈
                    return cards.get(0);
                return null;
            default:  // 默认逻辑：必须选就选一张，否则放弃
                return cards.get(0);
//                return forced ? cards.get(0) : null;
        }
        // return cards.get(0);
    }

    @Override
    protected int chooseNumber(int max, boolean forced) {
        return 1;
    }

}
