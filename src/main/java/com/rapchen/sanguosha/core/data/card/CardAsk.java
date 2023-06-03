package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Skill;

import java.util.Collections;
import java.util.HashSet;
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
    public Set< Class<? extends Card> > types = new HashSet<>();
    public Scene scene;  // 场景：使用/打出/技能/出牌阶段
    public Player player;  // 被要求的角色
    public boolean forced = false;  // 是否强制使用
    public String reason;  // 原因
    public String prompt;  // 提示词
    public Event event = null;  // 关联的事件，通常是要求技能卡时

    public Set<Skill> bannedSkills = new HashSet<>();  // 不可用的技能列表

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

    /** 适用于触发技要求技能牌的场景 */
    public CardAsk(Event event, Player player) {
        this.event = event;
        this.player = player;
        this.scene = Scene.SKILL;
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
