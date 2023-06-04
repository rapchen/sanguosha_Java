package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.SkillCard;
import com.rapchen.sanguosha.core.data.card.trick.Duel;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TransformSkill;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

import java.util.List;

/**
 * 貂蝉 TODO 离间
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class DiaoChan extends General {
    public DiaoChan() {
        super("DiaoChan", "貂蝉", Gender.FEMALE, Nation.QUN, 3);
        skills.add(LiJian.class);
        skills.add(BiYue.class);
    }

    // 离间: 出牌阶段限一次，你可以弃置一张牌并选择两名男性角色：若如此做，视为其中一名角色对另一名角色使用一张【决斗】，此【决斗】不能被【无懈可击】响应。 
    public static class LiJian extends TransformSkill {
        public LiJian() {
            super("LiJian", "离间");
            subSkills.add(new LiJianNulli());
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() < 1) return null;
            return Card.createVirtualCard(LiJianCard.class, chosenCards);
        }

        @Override
        public boolean usableInPlayPhase() {
            return !owner.hasUsed(LiJianCard.class, "phase");
        }
    }

    public static class LiJianCard extends SkillCard {
        public LiJianCard() {
            maxTargetCount = 2;
        }

        @Override
        public boolean canUseTo(Player source, Player target) {
            return target.gender == Gender.MALE;
        }

        @Override
        public void doUseToAll(CardUse use) {
            Player source = use.targets.get(0), target = use.targets.get(1);
            Duel duel = Card.createTmpCard(Duel.class);
            duel.skill = skill;
            skill.doLog();
            source.useCard(duel, List.of(target));
        }
    }

    // 离间的决斗不能被无懈效果
    public static class LiJianNulli extends TriggerSkill {
        public LiJianNulli() {
            super("LiJianNulli", "离间", new Timing[]{Timing.TARGET_CHOSEN});
            compulsory = true;
            visible = false;
        }

        @Override
        public void onTrigger(Event event) {
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            if (!(use.card.skill instanceof LiJian)) return;
            for (CardEffect effect : use.effects) {
                effect.xFields.put("CannotNullify", true);
            }
        }

        @Override
        public boolean canTrigger(Event event) {
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            return use.card.skill instanceof LiJian;
        }
    }

    public static class BiYue extends TriggerSkill {
        public BiYue() {
            super("BiYue", "闭月", new Timing[]{Timing.PHASE_BEGIN});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final Phase phase = (Phase) event.xFields.get("Phase");
            if (phase != Phase.PHASE_END) return;
            if (askForUse(owner)) {
                doLog();
                owner.drawCards(1);
            }
        }
    }
}
