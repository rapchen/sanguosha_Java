package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;

import java.util.ArrayList;
import java.util.List;

/**
 * 用作技。将0~N张牌当作一张牌使用
 * @author Chen Runwen
 * @time 2023/5/14 12:14
 */
public abstract class ServeAsSkill extends Skill {

    public int maxCardCount = 1;  // 最大可选牌数。

    private List<Card> chosenCards = new ArrayList<>();  // 当前已选中的卡牌。每次使用技能都会初始化

    public ServeAsSkill(String name, String nameZh) {
        super(name, nameZh);
    }

    /**
     * 判断一张卡牌是否可以被选中，用作转化
     * @param card 将要选择的卡牌
     * @return 是否可以选中
     */
    public boolean cardFilter(Card card) {
        return !chosenCards.contains(card);
    }

    /**
     * 返回转化后的卡牌
     * @return 转化后的卡牌。如果不能转化，
     */
    public Card serveAs() {
        return null;
    }

    /**
     * 出牌阶段是否可用。默认可用
     */
    public boolean usableInPlayPhase() {
        return true;
    }

    /**
     * 响应时是否可用。默认不可用
     * @param ask 当前的卡牌要求
     */
    public boolean usableAtResponse(CardAsk ask) {
        return false;
    }
}
