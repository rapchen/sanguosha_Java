package com.rapchen.sanguosha.core.skill;

/**
 * 时机enum。每个事件都有时机
 * @author Chen Runwen
 * @time 2023/5/14 12:42
 */
public enum Timing {
    // 游戏相关
    GAME_START,

    // 阶段相关
    PHASE_BEGIN,

    // 杀相关
    SLASH_DODGED,

    // 伤害相关
    DAMAGE_DOING,  // 造成伤害时
    DAMAGE_DONE,  // 造成伤害后
}
