package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardAsk;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 青龙偃月刀 Green Dragon Crescent Blade
 * @author Chen Runwen
 * @time 2023/5/14 0:16
 */
public class CrescentBlade extends Weapon {
    public CrescentBlade(Suit suit, Point point) {
        super(suit, point, "CrescentBlade", "青龙偃月刀", 3);
        skill = new CrescentBladeSkill();
    }

    // 当你使用的【杀】被【闪】抵消时，你可以对相同的目标再使用一张【杀】。
    private static class CrescentBladeSkill extends TriggerSkill {
        public CrescentBladeSkill() {
            super("CrescentBladeSkill", "青龙偃月刀", new Timing[]{Timing.SLASH_DODGED});
        }

        @Override
        public void onTrigger(Event event) {
            final Player target = (Player) event.xField.get("Target");
            CardAsk ask = new CardAsk(Slash.class, CardAsk.Scene.USE, owner,
                    "askForSlash", "你可以使用青龙偃月刀，对目标角色再使用一张杀：");
            Card card = owner.askForCard(ask);
            if (card != null) {
                doLog();
                owner.useCard(card, Collections.singletonList(target));
            }
        }
    }
}
