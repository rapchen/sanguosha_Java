package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.*;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.data.card.trick.Indulgence;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.*;

/**
 * 大乔
 * @author Chen Runwen
 * @time 2023/6/3 16:46
 */
public class DaQiao extends General {
    public DaQiao() {
        super("DaQiao", "大乔", Gender.MALE, Nation.QUN, 3);
        skills.add(GuoSe.class);
        skills.add(LiuLi.class);
    }

    // 国色: 你可以将一张♦牌当【乐不思蜀】使用。
    public static class GuoSe extends TransformSkill {
        public GuoSe() {
            super("GuoSe", "国色");
            maxCardCount = 1;
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.suit == Card.Suit.DIAMOND;
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() < 1) return null;
            return Card.createVirtualCard(Indulgence.class, chosenCards);
        }
    }

    // 流离: 每当你成为【杀】的目标时，你可以弃置一张牌并选择你攻击范围内为此【杀】合法目标（无距离限制）的一名角色：若如此做，该角色代替你成为此【杀】的目标。
    public static class LiuLi extends TriggerSkill {
        public LiuLi() {
            super("LiuLi", "流离", new Timing[]{Timing.TARGETED_CHOOSING});
            setTransSkill(new LiuLiTrans());
        }

        @Override
        public void onTrigger(Event event) {
            askForTransform(event);
        }

        @Override
        public boolean canTrigger(Event event) {
            // 成为杀的目标
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            return event.player == owner && use.card instanceof Slash;
        }
    }

    public static class LiuLiTrans extends TriggeredTransformSkill {
        public LiuLiTrans() {
            super(LiuLiCard.class);
        }
    }

    public static class LiuLiCard extends SkillCard {
        @Override
        public boolean canUseTo(Player source, Player target) {
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            // 不能是自己，要在自己攻击范围内，要是合法目标
            return target != source
                    && source.inRange(target)
                    && target != use.source && !use.targets.contains(target)
                    && use.card.validTarget(use.source, target);
        }

        @Override
        public void doEffect(CardEffect effect) {
            Player source = effect.getSource(), target = effect.target;
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            use.targets.remove(source);
            use.targets.add(target);
            skill.doLog("%s 成为了 %s 的目标", target, use.card);
        }
    }
}
