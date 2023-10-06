package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.SkillCard;
import com.rapchen.sanguosha.core.data.card.basic.Peach;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.TransformSkill;

/**
 * 华佗
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class HuaTuo extends General {
    public HuaTuo() {
        super("HuaTuo", "华佗", Gender.MALE, Nation.QUN, 3);
        skills.add(QingNang.class);
        skills.add(JiJiu.class);
    }

    // 青囊: 出牌阶段限一次，你可以弃置一张手牌并选择一名已受伤的角色：若如此做，该角色回复1点体力。
    public static class QingNang extends TransformSkill {
        public QingNang() {
            super("QingNang", "青囊");
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.place.isHand();
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() == 1) {
                return Card.createVirtualCard(QingNangCard.class, chosenCards);
            }
            return null;
        }

        @Override
        public boolean usableInPlayPhase() {
            return !owner.hasUsed(QingNangCard.class, "phase");
        }
    }

    public static class QingNangCard extends SkillCard {
        @Override
        public boolean canUseTo(Player source, Player target) {
            return target.injured();
        }

        @Override
        public void doEffect(CardEffect effect) {
            effect.target.doRecover(1);
        }
    }

    // 急救: 你的回合外，你可以将一张红色牌当【桃】使用。
    public static class JiJiu extends TransformSkill {
        public JiJiu() {
            super("JiJiu", "急救");
            maxCardCount = 1;
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.isRed();
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() == 1) {
                return Card.createVirtualCard(Peach.class, chosenCards);
            }
            return null;
        }

        @Override
        public boolean usableInPlayPhase() {
            return false;
        }

        @Override
        public boolean usableAtResponse(CardAsk ask) {
            return ask.contains(Peach.class) && !owner.isCurrentPlayer();
        }
    }

}
