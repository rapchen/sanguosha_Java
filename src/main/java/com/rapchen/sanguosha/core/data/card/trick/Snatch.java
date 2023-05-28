package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;
import lombok.extern.slf4j.Slf4j;

/**
 * 顺手牵羊
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
@Slf4j
public class Snatch extends ImmediateTrickCard {
    public Snatch(Suit suit, Point point) {
        super(suit, point);
        name = "Snatch";
        nameZh = "顺手牵羊";
    }

    @Override
    public boolean canUseTo(Player source, Player target) {
        return target != source && target.getCardCount("hej") > 0;
    }

    @Override
    public int distanceLimit(Player source, Player target) {
        return 1;
    }

    @Override
    public void doEffect(CardEffect effect) {
        Player source = effect.getSource(), target = effect.target;
        CardChoose choose = CardChoose.fromPlayer(source, target, "hej", true, name, "请选择要获取的牌：");
        Card card = choose.chooseOne();
        if (card == null) return;
        Engine.eg.moveCard(card, Place.HAND, source, name);
        log.info("{} 从 {} 处获得了 {}张牌：{}", source.name, target.name, 1, card);
    }

}
