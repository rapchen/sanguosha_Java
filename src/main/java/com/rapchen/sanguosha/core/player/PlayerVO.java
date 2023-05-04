package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.data.card.Card;

import java.util.List;

/**
 * 当前玩家视角下的其他角色
 * @author Chen Runwen
 * @time 2023/4/25 15:52
 */
public class PlayerVO {
    public int id;
    public String name;
    public int hp;
    public int maxHp;
    public int handCardCount;

    public PlayerVO(Player player) {
        this.id = player.id;
        this.name = player.name;
        this.hp = player.hp;
        this.maxHp = player.maxHp;
        this.handCardCount = player.handCards.size();
    }
}
