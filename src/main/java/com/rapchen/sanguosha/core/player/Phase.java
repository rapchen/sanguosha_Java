package com.rapchen.sanguosha.core.player;

/**
 * 阶段enum
 * @author Chen Runwen
 * @time 2023/5/12 10:28
 */
public enum Phase {
    PHASE_OFF_TURN("OffTurn", "回合外"),
    PHASE_PREPARE("Prepare", "准备阶段"),
    PHASE_JUDGE("Judge", "判定阶段"),
    PHASE_DRAW("Draw", "摸牌阶段"),
    PHASE_PLAY("Play", "出牌阶段"),
    PHASE_DISCARD("Discard", "弃牌阶段"),
    PHASE_END("End", "结束阶段");

    public final String name;
    public final String nameZh;

    Phase(String name, String nameZh) {
        this.name = name;
        this.nameZh = nameZh;
    }

    @Override
    public String toString() {
        return nameZh;
    }
}
