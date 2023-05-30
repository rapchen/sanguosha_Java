package com.rapchen.sanguosha.core.data.card.basic;

import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 桃
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class Peach extends BasicCard {
    public Peach(Suit suit, Point point) {
        super(suit, point);
        name = "Peach";
        nameZh = "桃";
        benefit = 100;
    }

    @Override
    public boolean canUseToOriginally(Player source, Player target) {
        // 对自己用；或者对濒死角色用
        return (target == source && target.hp < target.maxHp && target.phase == Phase.PHASE_PLAY)
                || target.hp <= 0;
    }

    @Override
    public void doEffect(CardEffect effect) {
        effect.target.doRecover(1);
    }

}
