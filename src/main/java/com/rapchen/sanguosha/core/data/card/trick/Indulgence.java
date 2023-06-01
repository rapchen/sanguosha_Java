package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.player.Player;
import lombok.extern.slf4j.Slf4j;

/**
 * 乐不思蜀
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
@Slf4j
public class Indulgence extends DelayedTrickCard {
    public Indulgence(Suit suit, Point point) {
        super(suit, point);
        name = "Indulgence";
        nameZh = "乐不思蜀";
    }

    @Override
    public void doDelayedEffect(CardEffect effect) {
        Player target = effect.target;
        Judgement judge = target.doJudge(nameZh, card -> card.suit != Suit.HEART);
        if (judge.success) {
            target.skipPhase(Phase.PHASE_PLAY);
        }
    }
}
