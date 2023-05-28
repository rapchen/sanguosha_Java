package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

import java.util.ArrayList;
import java.util.List;

/**
 * 麒麟弓 KylinBow
 * @author Chen Runwen
 * @time 2023/5/14 0:18
 */
public class KylinBow extends Weapon {
    public KylinBow(Suit suit, Point point) {
        super(suit, point, "KylinBow", "麒麟弓", 5);
        skill = new KylinBowSkill();
    }

    // 当你使用【杀】对目标角色造成伤害时，你可以弃置其装备区里的一张坐骑牌。
    private static class KylinBowSkill extends TriggerSkill {
        public KylinBowSkill() {
            super("KylinBowSkill", "麒麟弓", new Timing[]{Timing.DAMAGE_DOING});
        }

        @Override
        public void onTrigger(Event event) {
            // 检验：杀造成的伤害
            final Damage damage = (Damage) event.xFields.get("Damage");
            if (damage.effect == null || !(damage.effect.getCard() instanceof Slash)) return;
            Player target = damage.target;
            if (target == null) return;

            // 找马
            List<Card> horses = new ArrayList<>();
            Card horse = target.equips.get(SubType.EQUIP_HORSE_DEF);
            if (horse != null) horses.add(horse);
            horse = target.equips.get(SubType.EQUIP_HORSE_OFF);
            if (horse != null) horses.add(horse);
            if (horses.isEmpty()) return;

            Card card = owner.chooseCard(
                    new CardChoose(owner).in(horses)
                            .reason(name, String.format("是否使用麒麟弓，弃置 %s 的一匹马？", target)));
            if (card != null) {
                Engine.eg.moveToDiscard(card);
                doLog(String.format("弃置了 %s 的 %s", target, card));
            }
        }
    }

}
