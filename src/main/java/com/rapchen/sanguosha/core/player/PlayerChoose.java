package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Choose;
import com.rapchen.sanguosha.core.data.card.Card;

import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 角色选择对象。代表要求角色在一些角色中选择1~N个目标角色。
 * @author Chen Runwen
 * @time 2023/5/31 19:42
 */
public class PlayerChoose extends Choose<Player> {

    public Card card;  // 关联的牌（如为某张牌的使用选择目标）

    // ************** 构造
    public PlayerChoose(Player player) {
        super(player);
    }

    // ************* 链式编程 *************
    public PlayerChoose inOthers() {
        this.candidates = player.getOtherPlayers();
        return this;
    }

    public PlayerChoose inAll() {
        this.candidates = Engine.eg.getAllPlayers();
        return this;
    }

    /** 过滤candidates */
    public PlayerChoose filter(Predicate<Player> filter) {
        candidates = candidates.stream().filter(filter).collect(Collectors.toList());
        return this;
    }

    public PlayerChoose forced() {
        this.forced = true;
        return this;
    }

    public PlayerChoose count(int count) {
        this.count = count;
        return this;
    }

    public PlayerChoose reason(String reason, String prompt) {
        this.reason = reason;
        this.prompt = prompt;
        return this;
    }

    public PlayerChoose withField(String key, Object value) {
        this.xFields.put(key, value);
        return this;
    }
}
