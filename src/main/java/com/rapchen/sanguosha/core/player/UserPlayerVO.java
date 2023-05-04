package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.data.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前玩家视角下的本角色
 * @author Chen Runwen
 * @time 2023/4/25 15:52
 */
public class UserPlayerVO {
    public int id;
    public String name;
    public int hp;
    public int maxHp;
    public List<Card> handCards;
    // TODO 装备

    public UserPlayerVO(Player player) {
        this.id = player.id;
        this.name = player.name;
        this.hp = player.hp;
        this.maxHp = player.maxHp;
        this.handCards = player.handCards;
    }
}
