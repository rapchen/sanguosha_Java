package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.SkillCard;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.TransformSkill;

/**
 * 刘备 SHU001
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class LiuBei extends General {
    public LiuBei() {
        super("LiuBei", "刘备", Gender.MALE, Nation.SHU, 4);
        skills.add(RenDe.class);
    }

    // 仁德: 出牌阶段，你可以将至少一张手牌任意分配给其他角色。你于本阶段内以此法给出的手牌首次达到两张或更多后，你回复1点体力。
    public static class RenDe extends TransformSkill {
        public RenDe() {
            super("RenDe", "仁德");
            maxCardCount = 10000;
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.place.isHand();
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() == 0) return null;
            return Card.createVirtualCard(RenDeCard.class, chosenCards);
        }
    }

    public static class RenDeCard extends SkillCard {
        public RenDeCard() {
            willThrow = false;
        }

        @Override
        public void doEffect(CardEffect effect) {
            Player source = effect.getSource(), target = effect.target;
            target.obtain(subCards, skill.name);
            skill.doLog("交给 %s %s 张牌：%s", target, subCards.size(), Card.cardsToString(subCards));
            // 回复
            int beforeCount = source.phaseFields.getInt(skill.name + "_Count", 0);
            int add = subCards.size();
            if (beforeCount < 2 && beforeCount + add >= 2) {
                source.doRecover(1);
            }
            source.phaseFields.incr(skill.name + "_Count", add);
        }
    }

}
