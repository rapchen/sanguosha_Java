package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.SkillCard;
import com.rapchen.sanguosha.core.data.card.trick.Dismantlement;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.AIPlayer;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.TransformSkill;

import java.util.List;

/**
 * 黄盖
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class HuangGai extends General {
    public HuangGai() {
        super("HuangGai", "黄盖", Gender.MALE, Nation.WU, 4);
        skills.add(KuRou.class);
    }

    // 苦肉: 出牌阶段，你可以失去1点体力：若如此做，你摸两张牌。
    public static class KuRou extends TransformSkill {
        public KuRou() {
            super("KuRou", "苦肉");
            maxCardCount = 0;
        }

        @Override
        public Card serveAs() {
            // AI暂时不要用苦肉，会苦死 TODO 写个合适的
            if (owner instanceof AIPlayer) return null;
            return Card.createVirtualCard(KuRouCard.class, chosenCards);
        }
    }

    public static class KuRouCard extends SkillCard {
        @Override
        public List<Player> getFixedTargets(Player source) {
            return List.of(source);
        }

        @Override
        public void doEffect(CardEffect effect) {
            effect.target.loseHp(1);
            // TODO 多人局要考虑死亡角色无法摸牌
            effect.target.drawCards(2);
        }
    }
}
