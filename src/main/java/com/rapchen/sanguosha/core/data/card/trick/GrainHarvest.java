package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.player.Player;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 五谷丰登
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
@Slf4j
public class GrainHarvest extends ImmediateTrickCard {

    private List<Card> choiceCards;  // 五谷备选牌

    public GrainHarvest(Suit suit, Point point) {
        super(suit, point);
        name = "GrainHarvest";
        nameZh = "五谷丰登";
        benefit = 100;
    }

    @Override
    public boolean canUseTo(Player source, Player target) {
        return true;
    }

    @Override
    public void doUseToAll(CardUse use) {
        choiceCards = Engine.eg.getCardsFromDrawPile(use.targets.size());
        Engine.eg.moveCards(choiceCards, Place.HANDLE, null, name);
        log.info("{} 翻开了五谷牌：{}", use.source.name, Card.cardsToString(choiceCards));
    }

    @Override
    public void doEffect(CardEffect effect) {
        Player target = effect.target;
        if (choiceCards == null || choiceCards.isEmpty()) return;
        Card card = target.chooseCard(new CardChoose(target, choiceCards,
                true, "GrainHarvest", "请选择一张五谷牌："));
        choiceCards.remove(card);
        target.obtain(card, name);
        log.info("{} 获得了五谷牌：{}", target.name, card);
    }

    @Override
    public void doAfterUse(CardUse use) {
        if (!choiceCards.isEmpty()) {
            Engine.eg.moveToDiscard(choiceCards);
            log.info("{}张五谷牌被置入弃牌堆：{}", choiceCards.size(), Card.cardsToString(choiceCards));
        }
    }
}
