package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

/**
 * 卡牌对单使用对象。描述卡牌针对某一个人的使用，从属于CardUse
 * @author Chen Runwen
 * @time 2023/5/10 12:45
 */
public class CardUseToOne {
    public CardUse use;
    public Player target;

    public CardUseToOne(CardUse use, Player target) {
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
