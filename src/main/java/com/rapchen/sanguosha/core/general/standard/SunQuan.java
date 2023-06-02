package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.SkillCard;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.TransformSkill;

import java.util.List;

/**
 * 孙权
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class SunQuan extends General {
    public SunQuan() {
        super("SunQuan", "孙权", Gender.MALE, Nation.WU, 4);
        skills.add(ZhiHeng.class);
    }

    // 制衡: 出牌阶段限一次，你可以弃置至少一张牌：若如此做，你摸等量的牌。
    public static class ZhiHeng extends TransformSkill {
        public ZhiHeng() {
            super("ZhiHeng", "制衡");
            maxCardCount = 9999;
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() == 0) return null;
            return Card.createVirtualCard(ZhiHengCard.class, chosenCards);
        }

        @Override
        public boolean usableInPlayPhase() {
            return !owner.hasUsed(ZhiHengCard.class, "phase");
        }
    }

    public static class ZhiHengCard extends SkillCard {
        @Override
        public List<Player> getFixedTargets(Player source) {
            return List.of(source);
        }

        @Override
        public void doEffect(CardEffect effect) {
            effect.target.drawCards(subCards.size());
        }
    }
}
