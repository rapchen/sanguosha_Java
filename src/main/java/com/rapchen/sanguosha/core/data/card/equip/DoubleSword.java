package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.card.CardChoose;
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

    // 每当你指定异性角色为【杀】的目标后，你可以令其选择一项：弃置一张手牌，或令你摸一张牌。
    private static class DoubleSwordSkill extends TriggerSkill {
        public DoubleSwordSkill() {
            super("DoubleSwordSkill", "雌雄双股剑", new Timing[]{Timing.TARGET_CHOSEN});
        }

        @Override
        public void onTrigger(Event event) {
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            if (!(use.card instanceof Slash)) return;
            for (Player target : use.targets) {
                if (owner.gender == target.gender) continue;
                if (askForUse(owner)) {
                    CardChoose choose = new CardChoose(target).fromSelf("h")
                            .reason(name, String.format("%s 发动了 %s, 请弃置一张手牌，否则对方摸一张牌", owner, this));
                    if (!target.askForDiscard(choose)) {
                        owner.drawCards(1);
                    }
                }
            }
        }
    }

}
