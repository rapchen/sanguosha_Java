package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

import java.util.*;

/**
 * 卡牌移动对象
 * @author Chen Runwen
 * @time 2023/5/10 12:45
 */
public class CardMove {
    public Map<Card, Place> cardsPlace;  // 移动的卡牌和各自对应的原位置
    public Place targetPlace;
    public String reason;
    public Set<Player> loseLastHandcardPlayers = new HashSet<>();

    public CardMove(Map<Card, Place> cardsPlace, Place targetPlace, String reason) {
        this.cardsPlace = cardsPlace;
        this.targetPlace = targetPlace;
        this.reason = reason;
    }

    public CardMove(List<Card> cards, Place targetPlace, String reason) {
        this.cardsPlace = cardsToPlaces(cards);
        this.targetPlace = targetPlace;
        this.reason = reason;
    }

    private static Map<Card, Place> cardsToPlaces(List<Card> cards) {
        Map<Card, Place> places = new HashMap<>();
        for (Card card : cards) {
            places.put(card, card.place);
        }
        return places;
    }

    public boolean isFromPlayer(Player player) {
        for (Place place : cardsPlace.values()) {
            if (place == player.HAND || place == player.EQUIP) return true;
        }
        return false;
    }
}
