package com.rapchen.sanguosha.core.skill;

/**
 * 时机enum。每个事件都有时机
 * @author Chen Runwen
 * @time 2023/5/14 12:42
 */
public enum Timing {
    // 游戏相关
    GAME_START,

    // 阶段相关。player为当前回合角色
    PHASE_BEGIN,

    // 卡牌使用相关。player为使用者
    CARD_ASKED,  // 被要求打出（或使用闪）一张牌时。 CardAsk
    TARGET_CHOSEN,  // 牌指定目标后。修改目标应该在这之前。 CardUse
    CARD_USED,  // 卡牌使用结束。卡牌的响应也已经结束。 CardUse

    // 杀相关
    SLASH_DODGED,  // 杀被闪避时。

    // 伤害相关。player为伤害制造者
    DAMAGE_DOING,  // 造成伤害时
    DAMAGE_DONE,  // 造成伤害后

    // =============== 以下MD开头为修正时机 ===============
    MD_SLASH_LIMIT,  // 修正回合内杀的次数限制
    MD_DISTANCE,  // 修正距离。 Target
}
