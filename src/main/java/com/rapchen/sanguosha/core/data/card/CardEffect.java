package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

/**
 * 卡牌使用效果。描述卡牌针对某一个人的使用效果，一个CardUse通常会对每个对象产生一个CardEffect
 * @author Chen Runwen
 * @time 2023/5/10 12:45
 */
public class CardEffect {
    public CardUse use;
    public Player target;

    public CardEffect(CardUse use, Player target) {
        this.use = use;
        this.target = target;
    }

    public Card getCard() {
        return use.card;
    }

    public Player getSource() {
        return use.source;
    }

    @Override
    public String toString() {
        return use.source + "对" + target + "使用的" + use.card;
    }
}
