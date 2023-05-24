package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 转化技。将0~N张牌当作一张牌使用（可以是当作一张抽象的技能牌）
 * @author Chen Runwen
 * @time 2023/5/14 12:14
 */
@Slf4j
public abstract class TransformSkill extends Skill {

    public int maxCardCount = 1;  // 最大可选牌数。

    public List<Card> chosenCards = new ArrayList<>();  // 当前已选中的卡牌。每次使用技能都会初始化

    public TransformSkill(String name, String nameZh) {
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

    /**
     * 要求角色使用转化技选牌
     * @param ask 当前CardAsk对象
     * @return 转化后的卡牌
     */
    public Card askForTransform(CardAsk ask) {
        // 选择转化掉的卡牌
        chosenCards.clear();
        for (int i = 0; i < maxCardCount; i++) {
            List<Card> choices = owner.getCards(owner, "he").stream()
                    .filter(this::cardFilter).filter(card -> !chosenCards.contains(card)).toList();
            String prompt = String.format("你正在发动 %s, 请选择第%d张牌, 0停止选择：", nameZh, i+1);
            Card chosen = owner.chooseCard(choices, false, prompt, name);
            if (chosen == null) break;  // 放弃选择了，直接跳出
            chosenCards.add(chosen);
        }
        // 创建转化后的卡牌
        Card servedAs = serveAs();
        if (servedAs == null) return null;
        servedAs.setSkill(this);
        log.warn("{} 发动了 {}, 将 {} 当作 {}",
                owner, this, Card.cardsToString(chosenCards), servedAs);
        return servedAs;
    }
}
