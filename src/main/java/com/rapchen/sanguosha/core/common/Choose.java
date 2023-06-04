package com.rapchen.sanguosha.core.common;

import com.rapchen.sanguosha.core.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 选择对象。代表要求用户在多个T类型的对象中，选择1~N个选项
 * @author Chen Runwen
 * @time 2023/6/2 21:53
 */
public class Choose <T> {
    public List<T> candidates;  // 候选范围
    public int count = 1;  // 需要选择的数量
    public int minCount = -1;  // 最少可以选择的数量。少于这个数量时视为放弃。-1为必须选满。
    public Player player;  // 被要求做选择的角色
    public boolean forced = false;  // 是否强制选择
    public String reason;  // 原因
    public String prompt;  // 提示词
    public Fields xFields = new Fields();  // 额外字段

    protected final List<T> chosen = new ArrayList<>();  // 已选的选项

    // ************** 构造
    public Choose(Player player) {
        this.player = player;
    }

    public Choose(Player player, List<? extends T> candidates,
                        boolean forced, String reason, String prompt) {
        this.player = player;
        this.candidates = new ArrayList<>(candidates);
        this.forced = forced;
        this.reason = reason;
        this.prompt = prompt;
    }

    // ************* 链式编程 *************
    public Choose<T> in(List<? extends T> candidates) {
        this.candidates = new ArrayList<>(candidates);
        return this;
    }

    /**
     * 过滤candidates
     */
    public Choose<T> filter(Predicate<T> filter) {
        candidates = candidates.stream().filter(filter).collect(Collectors.toList());
        return this;
    }

    public Choose<T> forced() {
        this.forced = true;
        return this;
    }

    public Choose<T> count(int count) {
        this.count = count;
        return this;
    }

    public Choose<T> reason(String reason, String prompt) {
        this.reason = reason;
        this.prompt = prompt;
        return this;
    }

    public Choose<T> withField(String key, Object value) {
        this.xFields.put(key, value);
        return this;
    }
}
