package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

/**
 * 卡牌使用对象。描述一个角色对0~多个角色使用一张牌
 * @author Chen Runwen
 * @time 2023/5/10 12:45
 */
public class CardUse {
    public Card card;
    public Player source;
    public List<Player> targets;
    public Player currentTarget;

    public CardUse(Card card, Player source, List<Player> targets) {
        this.card = card;
        this.source = source;
        this.targets = targets;
    }

    @Override
    public String toString() {
        return source + "对" + Player.playersToString(targets) + "使用的" + card;
    }
}
