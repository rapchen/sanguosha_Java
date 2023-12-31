package com.rapchen.sanguosha.core.pack;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.basic.Dodge;
import com.rapchen.sanguosha.core.data.card.basic.Peach;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.data.card.equip.*;
import com.rapchen.sanguosha.core.data.card.trick.*;

import static com.rapchen.sanguosha.core.data.card.Card.Point.*;
import static com.rapchen.sanguosha.core.data.card.Card.Suit.*;

/**
 * 标准卡牌包
 * @author Chen Runwen
 * @time 2023/5/5 17:32
 */
public class StandardCards extends Package {
    public StandardCards() {
        super();
        name = "standardCards";
        nameZh = "标准包";
        isCardPack = true;
    }

    @Override
    public void init() {
        // TODO 配置化，用反射做牌堆
        addCard(new Slash(SPADE, POINT_7));
        addCard(new Slash(SPADE, POINT_8));
        addCard(new Slash(SPADE, POINT_8));
        addCard(new Slash(SPADE, POINT_9));
        addCard(new Slash(SPADE, POINT_9));
        addCard(new Slash(SPADE, POINT_10));
        addCard(new Slash(SPADE, POINT_10));

        addCard(new Slash(CLUB, POINT_2));
        addCard(new Slash(CLUB, POINT_3));
        addCard(new Slash(CLUB, POINT_4));
        addCard(new Slash(CLUB, POINT_5));
        addCard(new Slash(CLUB, POINT_6));
        addCard(new Slash(CLUB, POINT_7));
        addCard(new Slash(CLUB, POINT_8));
        addCard(new Slash(CLUB, POINT_8));
        addCard(new Slash(CLUB, POINT_9));
        addCard(new Slash(CLUB, POINT_9));
        addCard(new Slash(CLUB, POINT_10));
        addCard(new Slash(CLUB, POINT_10));
        addCard(new Slash(CLUB, POINT_J));
        addCard(new Slash(CLUB, POINT_J));

        addCard(new Slash(HEART, POINT_10));
        addCard(new Slash(HEART, POINT_10));
        addCard(new Slash(HEART, POINT_J));

        addCard(new Slash(DIAMOND, POINT_6));
        addCard(new Slash(DIAMOND, POINT_7));
        addCard(new Slash(DIAMOND, POINT_8));
        addCard(new Slash(DIAMOND, POINT_9));
        addCard(new Slash(DIAMOND, POINT_10));
        addCard(new Slash(DIAMOND, POINT_K));

        addCard(new Dodge(HEART, POINT_2));
        addCard(new Dodge(HEART, POINT_2));
        addCard(new Dodge(HEART, POINT_K));

        addCard(new Dodge(DIAMOND, POINT_2));
        addCard(new Dodge(DIAMOND, POINT_2));
        addCard(new Dodge(DIAMOND, POINT_3));
        addCard(new Dodge(DIAMOND, POINT_4));
        addCard(new Dodge(DIAMOND, POINT_5));
        addCard(new Dodge(DIAMOND, POINT_6));
        addCard(new Dodge(DIAMOND, POINT_7));
        addCard(new Dodge(DIAMOND, POINT_8));
        addCard(new Dodge(DIAMOND, POINT_9));
        addCard(new Dodge(DIAMOND, POINT_10));
        addCard(new Dodge(DIAMOND, POINT_J));
        addCard(new Dodge(DIAMOND, POINT_J));

        addCard(new Peach(HEART, POINT_3));
        addCard(new Peach(HEART, POINT_4));
        addCard(new Peach(HEART, POINT_6));
        addCard(new Peach(HEART, POINT_7));
        addCard(new Peach(HEART, POINT_8));
        addCard(new Peach(HEART, POINT_9));
        addCard(new Peach(HEART, POINT_Q));

        addCard(new Peach(DIAMOND, POINT_Q));

        // 锦囊
        addCard(new ArchersAttack(HEART, POINT_A));

        addCard(new BarbarianInvasion(SPADE, POINT_7));
        addCard(new BarbarianInvasion(SPADE, POINT_K));
        addCard(new BarbarianInvasion(CLUB, POINT_7));

        addCard(new PeachOrchard(HEART, POINT_A));

        addCard(new GrainHarvest(HEART, POINT_3));
        addCard(new GrainHarvest(HEART, POINT_4));

        addCard(new ExNihilo(HEART, POINT_7));
        addCard(new ExNihilo(HEART, POINT_8));
        addCard(new ExNihilo(HEART, POINT_9));
        addCard(new ExNihilo(HEART, POINT_J));

        addCard(new Dismantlement(SPADE, POINT_3));
        addCard(new Dismantlement(SPADE, POINT_4));
        addCard(new Dismantlement(SPADE, POINT_Q));
        addCard(new Dismantlement(CLUB, POINT_3));
        addCard(new Dismantlement(CLUB, POINT_4));
        addCard(new Dismantlement(HEART, POINT_Q));

        addCard(new Snatch(SPADE, POINT_3));
        addCard(new Snatch(SPADE, POINT_4));
        addCard(new Snatch(SPADE, POINT_J));
        addCard(new Snatch(DIAMOND, POINT_3));
        addCard(new Snatch(DIAMOND, POINT_4));

        addCard(new Duel(SPADE, POINT_A));
        addCard(new Duel(CLUB, POINT_A));
        addCard(new Duel(DIAMOND, POINT_A));

        addCard(new BorrowingKnife(CLUB, POINT_Q));
        addCard(new BorrowingKnife(CLUB, POINT_K));

        addCard(new Nullification(SPADE, POINT_J));
        addCard(new Nullification(CLUB, POINT_Q));
        addCard(new Nullification(CLUB, POINT_K));

        addCard(new Indulgence(SPADE, POINT_6));
        addCard(new Indulgence(CLUB, POINT_6));
        addCard(new Indulgence(HEART, POINT_6));

        addCard(new Lightning(SPADE, POINT_A));

        // 装备
        addCard(new DefensiveHorse(SPADE, POINT_5, "Jueying", "绝影"));
        addCard(new DefensiveHorse(CLUB, POINT_5, "Dilu", "的卢"));
        addCard(new DefensiveHorse(HEART, POINT_K, "Zhuahuangfeidian", "爪黄飞电"));
        addCard(new OffensiveHorse(HEART, POINT_5, "Chitu", "赤兔"));
        addCard(new OffensiveHorse(SPADE, POINT_K, "Dayuan", "大宛"));
        addCard(new OffensiveHorse(DIAMOND, POINT_K, "Zixing", "紫骍"));

        addCard(new ZhugeCrossbow(DIAMOND, POINT_A));
        addCard(new ZhugeCrossbow(CLUB, POINT_A));
        addCard(new QinggangSword(SPADE, POINT_6));
        addCard(new DoubleSword(SPADE, POINT_2));
        addCard(new CrescentBlade(SPADE, POINT_5));
        addCard(new SnakeSpear(SPADE, POINT_Q));
        addCard(new StoneAxe(DIAMOND, POINT_5));
        addCard(new DecoratedHalberd(DIAMOND, POINT_Q));
        addCard(new KylinBow(HEART, POINT_5));

        addCard(new EightDiagram(SPADE, POINT_2));
        addCard(new EightDiagram(CLUB, POINT_2));

        // EX卡牌
        addCard(new IceSword(SPADE, POINT_2));
        addCard(new RenWangShield(CLUB, POINT_2));
        addCard(new Lightning(HEART, POINT_Q));
        addCard(new Nullification(DIAMOND, POINT_Q));

    }
}
