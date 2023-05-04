package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色对象。可以是人或AI
 * @author Chen Runwen
 * @time 2023/4/24 18:04
 */
public abstract class Player {
    private final Engine engine;

    public int id;
    public String name;
    public int hp;
    public int maxHp;
    public List<Card> handCards;

    public Player(Engine engine, int id, String name) {
        this.engine = engine;
        this.id = id;
        this.name = name;
        this.handCards = new ArrayList<>();
        // TODO 武将
        this.hp = 4;
        this.maxHp = 4;
    }

    public void drawCards(int count) {
        for (int i = 0; i < count; i++) {
            Card card = engine.getCardFromDrawPile();
            handCards.add(card);
        }
        // TODO 获得牌事件：只触发一次就行
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player ").append(id).append('(').append(name)
                .append(") HP ").append(hp).append('/').append(maxHp)
                .append("\n手牌: ");
        for (Card card : handCards) {
            sb.append(card).append(", ");
        }
        return sb.toString();
    }
}
