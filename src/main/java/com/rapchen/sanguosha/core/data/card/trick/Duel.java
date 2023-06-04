package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;

/**
 * 决斗
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class Duel extends ImmediateTrickCard {
    public Duel(Suit suit, Point point) {
        super(suit, point);
        name = "Duel";
        nameZh = "决斗";
    }

    @Override
    public void doEffect(CardEffect effect) {
        Player source = effect.getSource(), target = effect.target;
        while (true) {
            if (!askForSlash(target, effect)) {
                source.doDamage(new Damage(effect));
                break;
            }
            if (!askForSlash(source, effect)) {
                target.doDamage(new Damage(target, source, 1, effect));
                break;
            }
        }
    }

    /** 要求角色打出杀响应决斗 */
    private boolean askForSlash(Player player, CardEffect effect) {
        // 需要的杀的数量。无双会修改
        int remainCount = 1;
        remainCount = Engine.eg.triggerModify(new Event(Timing.MD_CARD_ASK_COUNT, player)
                .withField("CardEffect", effect), remainCount);
        while (remainCount > 0) {
            if (!player.askForSlash()) return false;
            remainCount--;
        }
        return true;
    }

}
