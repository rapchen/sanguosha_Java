package com.rapchen.sanguosha.core.data;

import com.rapchen.sanguosha.core.data.card.Card;

/**
 * @author Chen Runwen
 * @time 2023/5/11 21:53
 */
public class Judgement {
    public Card card;
    public Boolean success;  // 为null代表无好坏

    public Judgement(Card card, Boolean success) {
        this.card = card;
        this.success = success;
    }
}
