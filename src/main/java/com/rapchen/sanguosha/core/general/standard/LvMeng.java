package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 吕蒙
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class LvMeng extends General {
    public LvMeng() {
        super("LvMeng", "吕蒙", Gender.MALE, Nation.WU, 4);
        skills.add(KeJi.class);
    }

    // 克己: 若你未于出牌阶段内使用或打出【杀】，你可以跳过弃牌阶段。
    public static class KeJi extends TriggerSkill {
        public KeJi() {
            super("KeJi", "克己", new Timing[]{
                    Timing.PHASE_BEFORE, Timing.CARD_USED, Timing.CARD_RESPONDED, Timing.TURN_END});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            if (event.timing == Timing.PHASE_BEFORE) {
                // 弃牌阶段前，判断是否可以发动
                final Phase phase = (Phase) event.xFields.get("Phase");
                if (phase != Phase.PHASE_DISCARD) return;
                if (owner.xFields.remove("KeJi_Failed") == Boolean.TRUE) return;
                if (askForUse(owner)) {
                    doLog();
                    owner.skipPhase(phase);
                }
            } else if (event.timing == Timing.TURN_END) {
                owner.xFields.remove("KeJi_Failed");
            } else {
                // 使用或打出【杀】，上一个克己失败标记
                Card card = null;
                if (event.timing == Timing.CARD_USED) {
                    final CardUse use = (CardUse) event.xFields.get("CardUse");
                    card = use.card;
                } else if (event.timing == Timing.CARD_RESPONDED) {
                    card = (Card) event.xFields.get("Card");
                }
                if (card instanceof Slash && Engine.eg.currentPlayer == owner) {
                    owner.xFields.put("KeJi_Failed", true);
                }
            }
        }
    }
}
