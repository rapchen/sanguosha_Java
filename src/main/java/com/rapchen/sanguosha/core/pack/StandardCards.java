package com.rapchen.sanguosha.core.pack;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.Dodge;
import com.rapchen.sanguosha.core.data.card.Slash;

/**
 * 标准卡牌包
 * @author Chen Runwen
 * @time 2023/5/5 17:32
 */
public class StandardCards extends Package {
    public StandardCards(Engine engine) {
        super(engine);
        name = "standardCards";
        nameZh = "标准包";
        isCardPack = true;
    }

    @Override
    public void init() {
        // TODO 配置化，用反射做牌堆
        addCard(new Slash(Card.Suit.SPADE, Card.Point.POINT_7));
        addCard(new Slash(Card.Suit.SPADE, Card.Point.POINT_8));
        addCard(new Slash(Card.Suit.SPADE, Card.Point.POINT_8));
        addCard(new Slash(Card.Suit.SPADE, Card.Point.POINT_9));
        addCard(new Slash(Card.Suit.SPADE, Card.Point.POINT_9));
        addCard(new Slash(Card.Suit.SPADE, Card.Point.POINT_10));
        addCard(new Slash(Card.Suit.SPADE, Card.Point.POINT_10));

        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_2));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_3));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_4));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_5));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_6));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_7));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_8));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_8));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_9));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_9));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_10));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_10));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_J));
        addCard(new Slash(Card.Suit.CLUB, Card.Point.POINT_J));

        addCard(new Slash(Card.Suit.HEART, Card.Point.POINT_10));
        addCard(new Slash(Card.Suit.HEART, Card.Point.POINT_10));
        addCard(new Slash(Card.Suit.HEART, Card.Point.POINT_J));

        addCard(new Slash(Card.Suit.DIAMOND, Card.Point.POINT_6));
        addCard(new Slash(Card.Suit.DIAMOND, Card.Point.POINT_7));
        addCard(new Slash(Card.Suit.DIAMOND, Card.Point.POINT_8));
        addCard(new Slash(Card.Suit.DIAMOND, Card.Point.POINT_9));
        addCard(new Slash(Card.Suit.DIAMOND, Card.Point.POINT_10));
        addCard(new Slash(Card.Suit.DIAMOND, Card.Point.POINT_K));

        addCard(new Dodge(Card.Suit.HEART, Card.Point.POINT_2));
        addCard(new Dodge(Card.Suit.HEART, Card.Point.POINT_2));
        addCard(new Dodge(Card.Suit.HEART, Card.Point.POINT_K));

        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_2));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_2));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_3));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_4));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_5));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_6));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_7));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_8));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_9));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_10));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_J));
        addCard(new Dodge(Card.Suit.DIAMOND, Card.Point.POINT_J));
    }
}
