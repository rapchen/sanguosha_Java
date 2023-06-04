package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.data.card.trick.Duel;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 吕布
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class LvBu extends General {
    public LvBu() {
        super("LvBu", "吕布", Gender.MALE, Nation.QUN, 4);
        skills.add(WuShuang.class);
    }

    // 无双: 锁定技，每当你指定【杀】的目标后，目标角色须使用两张【闪】抵消此【杀】。你指定或成为【决斗】的目标后，与你【决斗】的角色每次须连续打出两张【杀】。
    public static class WuShuang extends TriggerSkill {
        public WuShuang() {
            // 这里理论上发动时机是TARGET_CHOSEN。懒得搞了
            super("WuShuang", "无双", new Timing[]{Timing.MD_CARD_ASK_COUNT});
            compulsory = true;
        }

        @Override
        public int onModify(Event event, int value) {
            final CardEffect effect = (CardEffect) event.xFields.get("CardEffect");
            Player source = effect.getSource(), target = effect.target;
            if (effect.getCard() instanceof Slash) {
                if (source == owner) return 2;
            } else if (effect.getCard() instanceof Duel) {
                if (source == owner && target == event.player) return 2;
                if (target == owner && source == event.player) return 2;
            }
            return value;
        }

        @Override
        public boolean canTrigger(Event event) {
            // 指定或成为目标
            final CardEffect effect = (CardEffect) event.xFields.get("CardEffect");
            return effect.getSource() == owner || effect.target == owner;
        }
    }
}
