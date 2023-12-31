package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 夏侯惇
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class XiahouDun extends General {
    public XiahouDun() {
        super("XiahouDun", "夏侯惇", Gender.MALE, Nation.WEI, 4);
        skills.add(GangLie.class);
    }

    // 刚烈: 每当你受到伤害后，你可以进行判定：若结果不为♥，则伤害来源选择一项：弃置两张手牌，或受到1点伤害。
    public static class GangLie extends TriggerSkill {
        public GangLie() {
            super("GangLie", "刚烈", new Timing[]{Timing.DAMAGED_DONE});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final Damage damage = (Damage) event.xFields.get("Damage");
            Player source = damage.source;
            if (source == null) return;

            if (source.alive && askForUse(owner)) {
                doLog();
                CardChoose choose = new CardChoose(source).fromSelf("h")
                        .count(2).reason(name, "请弃置两张手牌，否则受到一点伤害");
                if (!source.askForDiscard(choose)) {
                    owner.doDamage(new Damage(owner, source, 1, name));
                }
            }
        }
    }
}
