package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.SkillCard;

/**
 * 仅在特定触发下可用的转化技，用于触发技的选牌和选目标。
 * @author Chen Runwen
 * @time 2023/6/3 20:04
 */
public class TriggeredTransformSkill extends TransformSkill {
    public TriggerSkill triggerSkill;
    public Class<? extends SkillCard> cardClass;

    public TriggeredTransformSkill(Class<? extends SkillCard> cardClass) {
        // name用类名，nameZh用对应触发技的中文名
        super("", "");
        this.cardClass = cardClass;
        this.name = this.getClass().getSimpleName();
        this.visible = false;
    }

    @Override
    public Card serveAs() {
        if (chosenCards.size() < maxCardCount) return null;
        SkillCard card = Card.createVirtualCard(cardClass, chosenCards);
        if (card != null) card.event = ask.event;
        return card;
    }

    @Override
    public boolean usableInPlayPhase() {
        return false;
    }
}
