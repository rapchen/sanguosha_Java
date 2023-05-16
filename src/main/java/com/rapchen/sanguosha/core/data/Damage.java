package com.rapchen.sanguosha.core.data;

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

    public Damage(Player source, Player target, int count) {
        this.source = source;
        this.target = target;
        this.count = count;
    }
}
