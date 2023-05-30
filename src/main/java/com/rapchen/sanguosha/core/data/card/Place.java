package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;

/**
 * 卡牌位置。包含位置和位置对应角色信息
 * @author Chen Runwen
 * @time 2023/5/30 14:56
 */
public class Place {
    public enum PlaceType {
        DRAW,  // 摸牌堆
        DISCARD,  // 弃牌堆
        HAND,  // 手牌
        EQUIP,  // 装备区
        JUDGE,  // 判定区
        JUDGE_CARD,  // 判定牌（临时，算是处理区的一种，但是有所属角色，可以触发红颜等视为技）
        HANDLE,  // 处理区（临时）
        EXTRA,  // 游戏外，角色的额外牌堆
    }

    public PlaceType type;
    public Player owner = null;  // 当前区域对应的角色（判定区和判定牌都算）。不属于某个角色则为null

    public final static Place DRAW = new Place(PlaceType.DRAW);
    public final static Place DISCARD = new Place(PlaceType.DRAW);
    public final static Place HANDLE = new Place(PlaceType.DRAW);

    public Place(PlaceType type) {
        this.type = type;
    }

    public Place(PlaceType type, Player owner) {
        this.type = type;
        this.owner = owner;
    }

    public boolean is(PlaceType placeType) {
        return type == placeType;
    }

    public boolean isHand() {
        return type == PlaceType.HAND;
    }
    public boolean isEquip() {
        return type == PlaceType.EQUIP;
    }
    public boolean isJudge() {
        return type == PlaceType.JUDGE;
    }
}
