package com.rapchen.sanguosha.core.data;

import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 伤害
 * @author Chen Runwen
 * @time 2023/5/17 0:58
 */
public class Damage {
    public Player source;
    public Player target;
    public int count;
    public String reason;
    public CardEffect effect;  // 伤害来自哪个卡牌效果
    public boolean avoided = false;  // 伤害是否被防止

    public Damage(Player source, Player target, int count, String reason) {
        this.source = source;
        this.target = target;
        this.count = count;
        this.reason = reason;
    }

    public Damage(Player source, Player target, int count, String reason, CardEffect effect) {
        this.source = source;
        this.target = target;
        this.count = count;
        this.reason = reason;
        this.effect = effect;
    }

    public Damage(CardEffect effect) {
        this(1, effect);
    }

    public Damage(int count, CardEffect effect) {
        this(effect.getSource(), effect.target, count, effect);
    }

    public Damage(Player source, Player target, int count, CardEffect effect) {
        this(source, target, count, effect.getCard().name, effect);
    }

    @Override
    public String toString() {
        return source + "对" + target + "造成的" + count + "点伤害";
    }
}
