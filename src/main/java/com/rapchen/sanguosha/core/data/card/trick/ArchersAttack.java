package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

/**
 * 万箭齐发
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class ArchersAttack extends ImmediateTrickCard {
    public ArchersAttack(Suit suit, Point point) {
        super(suit, point);
        name = "ArchersAttack";
        nameZh = "万箭齐发";
    }

    @Override
    public List<Player> getFixedTargets(Player source) {
        return source.getOtherPlayers();
    }

    @Override
    public void doEffect(CardEffect effect) {
        Player source = effect.getSource(), target = effect.target;
        if (!target.askForDodge(false)) {
            source.doDamage(new Damage(effect));
        }
    }

}
