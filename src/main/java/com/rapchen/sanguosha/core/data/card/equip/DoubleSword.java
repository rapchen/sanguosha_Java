package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 雌雄双股剑
 * @author Chen Runwen
 * @time 2023/5/14 0:16
 */
public class DoubleSword extends Weapon {
    public DoubleSword(Suit suit, Point point) {
        super(suit, point, "DoubleSword", "雌雄双股剑", 2);
        skill = new DoubleSwordSkill();
    }

    // 当你使用【杀】指定一名异性角色为目标后，你可以令其选择一项：弃一张手牌；或令你摸一张牌。
    private static class DoubleSwordSkill extends TriggerSkill {
        public DoubleSwordSkill() {
            super("DoubleSwordSkill", "雌雄双股剑", new Timing[]{Timing.TARGET_CHOSEN});
        }

        @Override
        public void onTrigger(Event event) {
            final CardUse use = (CardUse) event.xField.get("CardUse");
            if (!(use.card instanceof Slash)) return;
            for (Player target : use.targets) {
                if (owner.gender == target.gender) continue;
                if (askForUse(owner)) {
                    boolean discard = target.askForDiscard(1, target, false, "h",
                            String.format("%s 发动了 %s, 请弃置一张手牌，否则对方摸一张牌", owner, this), name);
                    if (!discard) owner.drawCards(1);
                }
            }
        }
    }

}
