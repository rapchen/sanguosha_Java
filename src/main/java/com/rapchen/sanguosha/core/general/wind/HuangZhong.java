package com.rapchen.sanguosha.core.general.wind;

import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 黄忠
 * @author Chen Runwen
 * @time 2023/10/6 11:42
 */
public class HuangZhong extends General {
    public HuangZhong() {
        super("HuangZhong", "黄忠", Gender.MALE, Nation.SHU, 4);
        skills.add(LieGong.class);
    }

    // 烈弓: 每当你于出牌阶段内指定【杀】的目标后，若目标角色的手牌数大于或等于你的体力值，或目标角色的手牌数小于或等于你的攻击范围，你可以令该角色不能使用【闪】响应此【杀】。 
    public static class LieGong extends TriggerSkill {
        public LieGong() {
            super("LieGong", "烈弓", new Timing[]{Timing.TARGET_CHOSEN});
        }

        @Override
        public void onTrigger(Event event) {
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            if (!(use.card instanceof Slash)) return;
            if (owner.phase != Phase.PHASE_PLAY) return;
            for (CardEffect effect : use.effects) {
                Player target = effect.target;
                if (target.handCards.size() >= owner.hp || target.handCards.size() <= owner.getRange()) {
                    if (askForUse(owner, target)) {
                        doLog(String.format("此杀不可被 %s 闪避", target));
                        // 设置不可被target闪避的字段
                        effect.xFields.put("CannotDodge", true);
                    }
                }
            }
        }
    }
}
