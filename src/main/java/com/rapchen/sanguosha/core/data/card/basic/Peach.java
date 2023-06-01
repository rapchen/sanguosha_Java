package com.rapchen.sanguosha.core.data.card.basic;

import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.player.Player;

import java.util.ArrayList;
import java.util.List;

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
    public List<Player> getFixedTargets(Player source) {
        // 出牌阶段合法目标只有自己，如果满血则不能使用，返回空列表。
        if (source.injured()) return List.of(source);
        else return new ArrayList<>();
    }

    @Override
    public void doEffect(CardEffect effect) {
        effect.target.doRecover(1);
    }

}
