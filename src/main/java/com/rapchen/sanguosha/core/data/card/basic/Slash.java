package com.rapchen.sanguosha.core.data.card.basic;

import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.player.Player;

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
        return super.canUseTo(source, target);  // TODO 算攻击范围
    }

    @Override
    public void doUseToAll(CardUse use) {
        use.source.slashTimes--;
    }

    @Override
    public void doUseToOne(Player source, Player target) {
        if (!target.askForDodge(true)) {
            source.doDamage(target, 1);
        }
    }
}
