package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.Place;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 延时类锦囊
 * @author Chen Runwen
 * @time 2023/5/5 18:43
 */
public abstract class DelayedTrickCard extends TrickCard {
    public DelayedTrickCard(Suit suit, Point point) {
        super(suit, point);
        subType = SubType.TRICK_DELAYED;
        willThrow = false;  // 不进入弃牌堆
    }

    @Override
    public boolean originalValidTarget(Player source, Player target) {
        // 延时类锦囊默认判断逻辑：判定区不存在同名锦囊。子类可以在此基础上加判断
        for (DelayedTrickCard card : target.judgeArea) {
            if (card.name.equals(name)) return false;
        }
        return true;
    }

    @Override
    public void doEffect(CardEffect effect) {
        // 延时类锦囊统一处理逻辑：添加到判定区最后
        Engine.eg.moveCard(this, effect.target.JUDGE, "useDelayedTrickCard");
    }

    /**
     * 延时类锦囊的延时效果。在判定阶段触发，而非在使用时触发。这个效果会被无懈
     */
    public void doDelayedEffect(CardEffect effect) {}

    /**
     * 延时类锦囊的后处理，默认是进弃牌堆，而闪电则是转移。这个效果不会被无懈
     */
    public void doAfterDelayedEffect(Player target) {
        Engine.eg.moveToDiscard(this, Place.PlaceType.JUDGE);
    }
}
