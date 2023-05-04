package com.rapchen.sanguosha.core.data.card;

import lombok.Data;

/**
 * @author Chen Runwen
 * @time 2023/4/14 12:17
 */
@Data
public abstract class Card {
    enum Color {
        NO_COLOR, RED, BLACK;
    }

    public enum Suit {
        SUIT_NO(Color.NO_COLOR, "无色"),
        HEART(Color.RED, "♥"),
        DIAMOND(Color.RED, "♦"),
        SPADE(Color.BLACK, "♠"),
        CLUB(Color.BLACK, "♣"),
        SUIT_NO_RED(Color.RED, "红"),
        SUIT_NO_BLACK(Color.BLACK, "黑");

        public final Color color;
        public final String token;

        Suit(Color color, String token) {
            this.color = color;
            this.token = token;
        }

        public static final Suit[] REGULAR_SUITS = new Suit[]{HEART, DIAMOND, SPADE, CLUB};

        @Override
        public String toString() {
            return token;
        }
    }

    public enum Point {
        POINT_NO("0"), POINT_A("A"), POINT_2("2"), POINT_3("3"), POINT_4("4"),
        POINT_5("5"), POINT_6("6"), POINT_7("7"), POINT_8("8"), POINT_9("9"),
        POINT_10("10"), POINT_J("J"), POINT_Q("Q"), POINT_K("K");
        public final String token;
        public static final Point[] REGULAR_POINTS = new Point[]{POINT_A, POINT_2, POINT_3, POINT_4, POINT_5,
                POINT_6, POINT_7, POINT_8, POINT_9, POINT_10, POINT_J, POINT_Q, POINT_K};

        Point(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return token;
        }
    }

    public Suit suit;
    public Point point;
    protected int id;  // 在牌堆里的唯一ID，从1开始。虚拟卡<0
    protected String name;  // 牌名。不考虑扩展包的情况下也可以用instanceof判断
    protected String nameZh;  // 中文牌名，用于显示。

    public Card(Suit suit, Point point, int id) {
        this.suit = suit;
        this.point = point;
        this.id = id;
    }

    @Override
    public String toString() {
        return nameZh + "[" + suit + point + "]" + id;
    }
}
