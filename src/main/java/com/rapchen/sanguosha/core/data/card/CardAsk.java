package com.rapchen.sanguosha.core.data.card;

import java.util.Collections;
import java.util.Set;

/**
 * 卡牌要求对象。代表要求角色使用/打出一张牌，或者要求角色使用一个技能卡。
 * @author Chen Runwen
 * @time 2023/5/20 15:22
 */
public class CardAsk {

    public static enum Scene {
        USE,  // 使用
        RESPONSE,  // 打出
        SKILL;  // 使用技能
    }

    // 要求的卡牌类型
    public Set< Class<? extends Card> > types;

    public Scene scene;

    public String reason;

    public CardAsk(Set<Class<? extends Card>> types, Scene scene, String reason) {
        this.types = types;
        this.scene = scene;
        this.reason = reason;
    }

    public CardAsk(Class<? extends Card> type, Scene scene, String reason) {
        this.types = Collections.singleton(type);
        this.scene = scene;
        this.reason = reason;
    }

    public boolean contains(Class<? extends Card> type) {
        return types.contains(type);
    }
}
