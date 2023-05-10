package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
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
    public void doUseToAll(CardUse use) {
        choiceCards = use.source.engine.getCardsFromDrawPile(use.targets.size());
        log.info("{} 翻开了五谷牌：{}", use.source.name, Card.cardsToString(choiceCards));
    }

    @Override
    public void doUseToOne(Player source, Player target) {
        if (choiceCards == null || choiceCards.isEmpty()) return;
        Card card = target.chooseCard(choiceCards, true, "请选择一张五谷牌：", "GrainHarvest");
        choiceCards.remove(card);
        target.handCards.add(card);
        log.info("{} 获得了五谷牌：{}", target.name, card);
    }

    @Override
    public void doAfterUse(Player source, List<Player> targets) {
        if (!choiceCards.isEmpty()) {
            source.engine.table.discardPile.addAll(choiceCards);
            log.info("{}张五谷牌被置入弃牌堆：{}", choiceCards.size(), Card.cardsToString(choiceCards));
        }
    }
}
