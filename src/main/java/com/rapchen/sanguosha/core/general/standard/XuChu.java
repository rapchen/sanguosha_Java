package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.data.card.trick.Duel;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 许褚
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class XuChu extends General {
    public XuChu() {
        super("XuChu", "许褚", Gender.MALE, Nation.WEI, 4);
        skills.add(LuoYi.class);
    }

    // 裸衣: 摸牌阶段，你可以少摸一张牌：若如此做，本回合你使用【杀】或【决斗】对目标角色造成伤害时，此伤害+1。
    public static class LuoYi extends TriggerSkill {
        public LuoYi() {
            super("LuoYi", "裸衣", new Timing[]{
                    Timing.MD_DRAW_COUNT, Timing.TURN_END, Timing.DAMAGE_BEFORE});
        }

        @Override
        public int onModify(Event event, int value) {
            if (value >= 1 && askForUse(owner)) {
                doLog();
                owner.xFields.put(name, true);  // 加上裸衣标记
                return value - 1;
            }
            return value;
        }

        @Override
        public void onTrigger(Event event) {
            if (!owner.xFields.containsKey(name)) return;  // 必须有裸衣标记
            if (event.timing == Timing.DAMAGE_BEFORE) {
                final Damage damage = (Damage) event.xFields.get("Damage");
                CardEffect effect = damage.effect;
                if (effect == null) return;
                if (!(effect.getCard() instanceof Slash || effect.getCard() instanceof Duel)) return;
                if (effect.getSource() != owner) return;
                // TODO 判断连环传导
                doLog(String.format("伤害由 %d 点变为 %d 点", damage.count , damage.count+1));
                damage.count += 1;
            } else if (event.timing == Timing.TURN_END) {
                // TODO 失去技能的人也要正常移除标记。可以加个添加限时标记的逻辑
                owner.xFields.remove(name);
            }
        }
    }
}
