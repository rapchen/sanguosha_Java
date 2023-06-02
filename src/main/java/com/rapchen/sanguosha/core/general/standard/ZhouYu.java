package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.SkillCard;
import com.rapchen.sanguosha.core.data.card.basic.Peach;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TransformSkill;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

import java.util.Arrays;
import java.util.List;

/**
 * 周瑜
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class ZhouYu extends General {
    public ZhouYu() {
        super("ZhouYu", "周瑜", Gender.MALE, Nation.QUN, 3);
        skills.add(YingZi.class);
        skills.add(FanJian.class);
    }

    // 英姿: 摸牌阶段，你可以额外摸一张牌。
    public static class YingZi extends TriggerSkill {
        public YingZi() {
            super("YingZi", "英姿", new Timing[]{Timing.MD_DRAW_COUNT});
            useByDefault = true;
        }

        @Override
        public int onModify(Event event, int value) {
            if (askForUse(owner)) {
                doLog();
                return value + 1;
            }
            return value;
        }
    }

    // 反间: 出牌阶段限一次，你可以令一名其他角色选择一种花色，然后正面朝上获得你的一张手牌。若此牌花色与该角色所选花色不同，该角色受到1点伤害。
    public static class FanJian extends TransformSkill {
        public FanJian() {
            super("FanJian", "反间");
            maxCardCount = 0;
        }

        @Override
        public Card serveAs() {
            return Card.createVirtualCard(FanJianCard.class, chosenCards);
        }

        @Override
        public boolean usableInPlayPhase() {
            return owner.getCardCount("h") > 0 && !owner.hasUsed(FanJianCard.class, "phase");
        }
    }

    public static class FanJianCard extends SkillCard {
        @Override
        public void doEffect(CardEffect effect) {
            Player source = effect.getSource(), target = effect.target;
            // 选花色
            Suit suit = target.askForChoice(Arrays.asList(Suit.REGULAR_SUITS), true, "请选择一个花色", name);
            // 抽牌
            int num = Engine.eg.random.nextInt(source.handCards.size());
            Card card = source.handCards.get(num);
            target.obtain(card, name);
            // 伤害
            boolean willDamage = card.suit != suit;
            skill.doLog(String.format("%s 选择了 %s, 获得了 %s, %s", target, suit, card, willDamage ? "将受到1点伤害" : "未受到伤害"));
            if (willDamage) {
                source.doDamage(new Damage(effect));
            }
        }
    }

}
