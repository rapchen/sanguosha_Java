package com.rapchen.sanguosha.core.data.card.basic;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;

/**
 * 杀
 * @author Chen Runwen
 * @time 2023/4/24 22:23
 */
public class Slash extends BasicCard {

    public Slash(Suit suit, Point point) {
        super(suit, point);
        name = "Slash";
        nameZh = "杀";
        subType = SubType.BASIC;
    }

    @Override
    public boolean validInPlayPhase(Player player) {
        return player.slashTimes > 0;
    }

    @Override
    public boolean canUseTo(Player source, Player target) {
        return super.canUseTo(source, target)
                && source.getDistance(target) <= source.getRange();
    }

    @Override
    public void doUseToAll(CardUse use) {
        use.source.slashTimes--;
    }

    @Override
    public void doEffect(Player source, Player target) {
        boolean dodged = target.askForDodge(true);
        if (dodged) {  // 触发杀被闪避事件
            Engine.eg.invoke(new Event(Timing.SLASH_DODGED, source).withField("Target", target));
        }
        if (!dodged || source.xFields.containsKey("Slash_Undodged")) {
            source.doDamage(target, 1);
            source.xFields.remove("Slash_Undodged");  // 贯石斧标记
        }
    }
}
