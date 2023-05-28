package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 卡牌选择对象。代表要求角色在一些牌中选择1~N张牌。
 * @author Chen Runwen
 * @time 2023/5/20 15:22
 */
public class CardChoose {

    public List<Card> candidates;  // 候选卡牌范围
    public int count = 1;  // 选择卡牌数量
//    public Scene scene;  // 场景：使用/打出/技能/出牌阶段
    public Player player;  // 被要求的角色
    public boolean forced = false;  // 是否强制选择
    public String reason;  // 原因
    public String prompt;  // 提示词
    public Player target = null;  // 候选牌的目标角色（没有则为null）
    public Fields xFields = new Fields();  // 额外字段

    private final List<Card> chosen = new ArrayList<>();  // 已选的牌
    //    public int chosenTimes = 0;  // 已选的牌
//    public boolean gaveUp = false;  // 是否已放弃（有一次没选牌就是放弃了）

    // ************** 构造选择
    public CardChoose(Player player, List<? extends Card> candidates,
                      boolean forced, String reason, String prompt) {
        this.player = player;
        this.candidates = new ArrayList<>(candidates);
        this.forced = forced;
        this.reason = reason;
        this.prompt = prompt;
    }

    public CardChoose(Player player) {
        this.player = player;
    }

    // ************* 链式编程 *************
    public CardChoose in(List<? extends Card> candidates) {
        this.candidates = new ArrayList<>(candidates);
        return this;
    }

    public CardChoose fromPlayer(Player target, String pattern) {
        this.candidates = player.getCards(target, pattern);
        // 设置Target目标角色，有这个字段说明是从fromPlayer建的
        this.target = target;
        return this;
    }

    /** 从一名角色处选牌 */
    public CardChoose fromPlayer(Player target, Predicate<Card> filter) {
        candidates = target.getCards(target, "hej")
                .stream().filter(filter).collect(Collectors.toList());
        // 设置Target目标角色，有这个字段说明是从fromPlayer建的
        this.target = target;
        return this;
    }

    public CardChoose fromSelf(String pattern) {
        return fromPlayer(player, pattern);
    }

    /** 过滤candidates */
    public CardChoose filter(Predicate<Card> filter) {
        candidates = candidates.stream().filter(filter).collect(Collectors.toList());
        return this;
    }

    public CardChoose forced() {
        this.forced = true;
        return this;
    }

    public CardChoose count(int count) {
        this.count = count;
        return this;
    }

    public CardChoose reason(String reason, String prompt) {
        this.reason = reason;
        this.prompt = prompt;
        return this;
    }

    public CardChoose withField(String key, Object value) {
        this.xFields.put(key, value);
        return this;
    }

    // ************** 执行选择

    /**
     * 执行选择，返回所有选择的牌
     * @return 所有选择的牌。如果放弃，返回null
     */
    public List<Card> choose() {
        String basePrompt = prompt;  // 基本提示词（选多张的时候后面要带上张数信息）
        for (int i = 0; i < count; i++) {
            prompt = basePrompt + "(" + (i+1) + "/" + count + ")";
            Card card = chooseOne();
            // 目前是选不满就视为失败 TODO 【天命】之类不足全弃的逻辑需要另外写
            if (card == null) return null;  // 没有选牌，终止选择，返回null
            chosen.add(card);
            candidates.remove(card);  // 从候选牌中移除卡牌，避免重复选择
        }
        return chosen;
    }

    /**
     * 选择一张，返回选择的卡牌
     */
    public Card chooseOne() {
        if (candidates.isEmpty()) return null;  // 无牌可选
        Card card = player.chooseCard(this);  // 要求角色选牌
        if (card == null) return null;  // 角色放弃选牌
        // 对于虚假牌（如所有手牌）自动随机其中一张子卡
        if (card instanceof FakeCard) {
            // if (card.subCards.isEmpty()) return null;  // 不应该有empty的情况
            int num = Engine.eg.random.nextInt(card.subCards.size());
            card = card.subCards.get(num);
        }
        return card;
    }
}
