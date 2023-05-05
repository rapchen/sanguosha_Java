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
        StringBuilder sb = new StringBuilder();
        sb.append("摸牌堆: ");
        for (Card card : drawPile) {
            sb.append(card).append(", ");
        }
        sb.append("\n弃牌堆: ");
        for (Card card : discardPile) {
            sb.append(card).append(", ");
        }
        return sb.toString();
    }

    public String printForPlayer() {
        StringBuilder sb = new StringBuilder();
        sb.append("摸牌堆: ").append(drawPile.size());
        sb.append(", 弃牌堆: ").append(discardPile.size());
        return sb.toString();
    }
}
