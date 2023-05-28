package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.player.Player;

import java.util.*;

/**
 * 卡牌选择对象。代表要求角色在一些牌中选择1~N张牌。
 * @author Chen Runwen
 * @time 2023/5/20 15:22
 */
public class CardChoose <T extends Card> {

    public List<T> cards;  // 选择卡牌范围
    public int count = 1;  // 选择卡牌数量
//    public Scene scene;  // 场景：使用/打出/技能/出牌阶段
    public Player player;  // 被要求的角色
    public boolean forced = false;  // 是否强制选择
    public String reason;  // 原因
    public String prompt;  // 提示词
    public Fields xFields = new Fields();  // 额外字段

    public CardChoose(Player player, List<T> cards, boolean forced, String reason, String prompt) {
        this.player = player;
        this.cards = cards;
        this.forced = forced;
        this.reason = reason;
        this.prompt = prompt;
    }

    /** 添加字段，支持链式编程 */
    public CardChoose<T> withField(String key, Object value) {
        this.xFields.put(key, value);
        return this;
    }

    public static CardChoose<Card> fromPlayer(Player source, Player target, String pattern,
                                                    boolean forced, String reason, String prompt) {
        List<Card> cards1 = target.getCards(target, pattern);
        CardChoose<Card> choose = new CardChoose<>(source, cards1, forced, reason, prompt);
        // 设置Target目标角色，有这个字段说明是从fromPlayer建的
        choose.xFields.put("Target", target);
        return choose;
    }
}
