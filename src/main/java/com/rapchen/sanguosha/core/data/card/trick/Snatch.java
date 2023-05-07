package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
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
    public boolean canUseInPlayPhase(Player player) {
        // TODO 顺手距离是1
        return player.getOtherPlayers().get(0).handCards.size() > 0;
    }

    @Override
    public void doUseToOne(Player source, Player target) {
        Card card = source.askForCardFromPlayer(target, "请选择要获取的牌：", true);
        if (card == null) return;
        if (card.isVirtual()) {
            int num = source.engine.random.nextInt(card.subCards.size());
            card = card.subCards.get(num);
        }
        target.handCards.remove(card);  // TODO 可以加一个失去牌的逻辑，从各种地方都遍历一下移除
        source.handCards.add(card);  // TODO 获得牌的时机
        log.info("{} 从 {} 处获得了 {}张牌：{}", source.name, target.name, 1, card);
    }

}
