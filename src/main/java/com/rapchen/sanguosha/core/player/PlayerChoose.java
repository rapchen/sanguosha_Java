package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.data.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 角色选择对象。代表要求角色在一些角色中选择1~N个目标角色。
 * @author Chen Runwen
 * @time 2023/5/31 19:42
 */
public class PlayerChoose {

    public List<Player> candidates;  // 候选角色范围
    public int count = 1;  // 选择角色数量
    public Player player;  // 被要求做选择的角色
    public boolean forced = false;  // 是否强制选择
    public String reason;  // 原因
    public Card card;  // 关联的牌（如为某张牌的使用选择目标）
    public String prompt;  // 提示词
    public Fields xFields = new Fields();  // 额外字段

    private final List<Player> chosen = new ArrayList<>();  // 已选的角色

    // ************** 构造
    public PlayerChoose(Player player, List<? extends Player> candidates,
                        boolean forced, String reason, String prompt) {
        this.player = player;
        this.candidates = new ArrayList<>(candidates);
        this.forced = forced;
        this.reason = reason;
        this.prompt = prompt;
    }

    public PlayerChoose(Player player) {
        this.player = player;
    }

    // ************* 链式编程 *************
    public PlayerChoose in(List<? extends Player> candidates) {
        this.candidates = new ArrayList<>(candidates);
        return this;
    }

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
