package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.*;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TransformSkill;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 张辽
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class ZhangLiao extends General {
    public ZhangLiao() {
        super("ZhangLiao", "张辽", Gender.MALE, Nation.WEI, 4);
        skills.add(TuXi.class);
    }

    // 突袭: 摸牌阶段开始时，你可以放弃摸牌并选择一至两名有手牌的其他角色：若如此做，你依次获得这些角色各一张手牌。
    public static class TuXi extends TriggerSkill {
        public TuXi() {
            super("TuXi", "突袭", new Timing[]{Timing.PHASE_BEGIN});
            setTransformSkill(new TuXiTrans());
        }

        @Override
        public void onTrigger(Event event) {
            final Phase phase = (Phase) event.xFields.get("Phase");
            if (phase != Phase.PHASE_DRAW) return;
            askForTransform(owner);
        }

        @Override
        public boolean canTrigger(Event event) {
            return super.canTrigger(event) && owner.phaseFields.get("DrawPhase_SkipDraw") != Boolean.TRUE;
        }
    }
    
    public static class TuXiTrans extends TransformSkill {
        public TuXiTrans() {
            super("TuXiTrans", "突袭");
            maxCardCount = 0;
            visible = false;
        }

        @Override
        public Card serveAs() {
            return Card.createVirtualCard(TuXiCard.class, chosenCards);
        }

        @Override
        public boolean usableInPlayPhase() {
            return false;
        }
    }

    public static class TuXiCard extends SkillCard {
        public TuXiCard() {
            maxTargetCount = 2;
        }

        @Override
        public boolean canUseTo(Player source, Player target) {
            return target != source && target.getCardCount("h") > 0;
        }

        @Override
        public boolean targetsValid() {
            return chosenTargets.size() > 0 && chosenTargets.size() <= 2;
        }

        @Override
        public void doUseToAll(CardUse use) {
            // 放弃摸牌
            use.source.phaseFields.put("DrawPhase_SkipDraw", true);
        }

        @Override
        public void doEffect(CardEffect effect) {
            Player source = effect.getSource(), target = effect.target;
            int num = Engine.eg.random.nextInt(target.handCards.size());
            Card card = target.handCards.get(num);
            source.obtain(card, name);
            skill.doLog("从 %s 处获得了 %s", target, card);
        }
    }

}
