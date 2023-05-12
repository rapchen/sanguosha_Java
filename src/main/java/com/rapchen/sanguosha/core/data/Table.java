package com.rapchen.sanguosha.core.data;

import com.rapchen.sanguosha.core.data.card.Card;
import lombok.Data;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 牌桌。摸牌堆弃牌堆
 * @author Chen Runwen
 * @time 2023/4/14 12:13
 */
@Data
public class Table {
    public Deque<Card> drawPile;
    public Deque<Card> discardPile;

    public Table() {
        drawPile = new ArrayDeque<>();
        discardPile = new ArrayDeque<>();
    }

    public String print() {
        return "摸牌堆: " + Card.cardsToString(drawPile) +
                "\n弃牌堆: " + Card.cardsToString(discardPile);
    }

    public String printForPlayer() {
        return "摸牌堆: " + drawPile.size() +
                ", 弃牌堆: " + Card.cardsToString(discardPile);
    }
}
