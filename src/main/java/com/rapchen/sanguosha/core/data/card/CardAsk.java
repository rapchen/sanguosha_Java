package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

import java.util.Collections;
import java.util.Set;

/**
 * 卡牌要求对象。代表要求角色使用/打出一张牌，或者要求角色使用一个技能卡。
 * @author Chen Runwen
 * @time 2023/5/20 15:22
 */
public class CardAsk {

    public static enum Scene {
        PLAY,  // 出牌阶段
        USE,  // 使用
        RESPONSE,  // 打出
        SKILL;  // 使用技能
    }

    // 要求的卡牌类型
    public Set< Class<? extends Card> > types;

    public Scene scene;  // 场景：使用/打出/技能/出牌阶段

    public Player player;  // 被要求的角色

    public String reason;  // 原因

    public String prompt;  // 提示词

    public CardAsk(Set<Class<? extends Card>> types, Scene scene, Player player, String reason, String prompt) {
        this.types = types;
        this.scene = scene;
        this.player = player;
        this.reason = reason;
        this.prompt = prompt;
    }

    public CardAsk(Class<? extends Card> type, Scene scene, Player player, String reason, String prompt) {
        this(Collections.singleton(type), scene, player, reason, prompt);
    }

    public CardAsk(Scene scene, Player player) {
        this.scene = scene;
        this.player = player;
    }

    /**
     * 判断CardAsk是否包含某一类卡牌
     */
    public boolean contains(Class<? extends Card> type) {
        for (Class<? extends Card> askType : types) {
            if (askType.isAssignableFrom(type)) return true;  // 判断是否子类，如火杀也能响应要求杀的场合
        }
        return false;
    }

    /**
     * 判断一张牌是否能响应这个CardAsk
     */
    public boolean matches(Card card) {
        for (Class<? extends Card> askType : types) {
            if (askType.isInstance(card)) return true;  // 判断是否实例
        }
        return false;
    }
}
