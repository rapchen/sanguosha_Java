package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.data.card.SkillCard;
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
    public CardAsk ask = null;  // 当前卡牌要求场景

    public TransformSkill(String name, String nameZh) {
        super(name, nameZh);
    }

    /**
     * 判断一张卡牌是否可以被选中，用作转化
     * @param card 将要选择的卡牌
     * @return 是否可以选中。默认都能选
     */
    public boolean cardFilter(Card card) {
        return true;
    }

    /**
     * 返回额外卡牌选择范围。默认范围为使用者的手牌和装备牌，其他可选牌需要放到这里
     */
    public List<Card> extraCards() {
        return null;
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
        this.ask = ask;  // 设置当前请求，在选牌和转化时需要使用
        for (int i = 0; i < maxCardCount; i++) {
            String prompt = String.format("你正在发动 %s, 请选择第%d张牌, 0停止选择：", nameZh, i+1);
            Card chosen = owner.chooseCard(
                    new CardChoose(owner).fromSelf("he").add(extraCards())
                            .filter(card -> !chosenCards.contains(card) && this.cardFilter(card))
                            .reason(name, prompt));
            if (chosen == null) break;  // 放弃选择了，直接跳出
            chosenCards.add(chosen);
        }
        // 创建转化后的卡牌
        Card servedAs = serveAs();
        this.ask = null;
        if (servedAs == null) {
            ask.bannedSkills.add(this);  // 转化失败，本次ask剔除该技能选项
            return null;
        }
        servedAs.setSkill(this);
        if (servedAs.nameZh == null) servedAs.nameZh = nameZh;
        if (!(servedAs instanceof SkillCard)) {
            doLog(String.format("将 %s 当作 %s",
                    Card.cardsToString(chosenCards), servedAs));
        }
        return servedAs;
    }
}
