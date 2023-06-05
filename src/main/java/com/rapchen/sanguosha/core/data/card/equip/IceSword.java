package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 寒冰剑
 * @author Chen Runwen
 * @time 2023/5/14 0:18
 */
public class IceSword extends Weapon {
    public IceSword(Suit suit, Point point) {
        super(suit, point, "IceSword", "寒冰剑", 2);
        skill = new IceSwordSkill();
    }

    // 每当你使用【杀】对目标角色造成伤害时，若该角色有牌，你可以防止此伤害，然后依次弃置其两张牌。
    private static class IceSwordSkill extends TriggerSkill {
        public IceSwordSkill() {
            super("IceSwordSkill", "寒冰剑", new Timing[]{Timing.DAMAGE_DOING});
        }

        @Override
        public void onTrigger(Event event) {
            // 检验：杀造成的伤害
            final Damage damage = (Damage) event.xFields.get("Damage");
            if (damage.effect == null || !(damage.effect.getCard() instanceof Slash)) return;
            Player target = damage.target;
            if (target == null || target.getCardCount("he") == 0) return;

            if (askForUse(owner)) {
                doLog("防止了 %s", damage);
                damage.avoided = true;
                // 依次弃置2张
                owner.askForDiscard(
                        new CardChoose(owner).fromPlayer(target, "he")
                                .forced().reason(name, null));
                if (target.getCardCount("he") > 0) {
                    owner.askForDiscard(
                            new CardChoose(owner).fromPlayer(target, "he")
                                    .forced().reason(name, null));
                }
            }
        }
    }

}
