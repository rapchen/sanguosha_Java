package com.rapchen.sanguosha.core.skill;

/**
 * 时机enum。每个事件都有时机
 * @author Chen Runwen
 * @time 2023/5/14 12:42
 */
public enum Timing {
    // 游戏相关
    GAME_START,

    // 回合、阶段相关。player为当前回合角色
    TURN_BEGIN,
    TURN_END,
    PHASE_BEFORE,  // 阶段开始前时机。可以进行阶段的跳过和插入。 Phase
    PHASE_BEGIN,  // 阶段开始时机。 Phase

    // 卡牌使用相关。player为使用者
    CARD_ASKED,  // 被要求打出（或使用闪）一张牌时。 CardAsk
    CARD_USING,  // 卡牌使用时立刻触发。早于修改目标的时机。 CardUse
    TARGET_CHOSEN,  // 牌指定目标后。修改目标应该在这之前。 CardUse
    CARD_USED,  // 卡牌使用结束。卡牌的响应也已经结束。 CardUse

    CARD_RESPONDED,  // 卡牌打出结束。Player为打出者 Card

    // 卡牌移动相关。Player为移动目标位置所属角色（可为null）
    CARD_MOVED,  // 卡牌移动后。 CardMove

    // 杀相关
    SLASH_DODGED,  // 杀被闪避时。

    // 伤害相关。DAMAGE_时机player为伤害制造者；DAMAGED_时机player为受伤者。
    DAMAGE_BEFORE,  // 造成伤害前。适合各种修改伤害的效果。 Damage
    DAMAGE_DOING,  // 造成伤害时。 Damage
    DAMAGE_DONE,  // 造成伤害后。 Damage
    DAMAGED_DONE,  // 受到伤害后。 Damage

    // 判定相关。
    JUDGE_BEFORE_EFFECT,  // 判定牌生效前。改判时机。 Judge
    JUDGE_DONE,  // 判定牌生效后。 Judge

    // =============== 以下MD开头为修正时机 ===============
    MD_SLASH_LIMIT,  // 修正回合内杀的次数限制
    MD_DISTANCE,  // 修正距离。Player为使用者。 Target
    MD_DISTANCE_LIMIT,  // 修正卡牌使用的距离限制。Player为使用者。 Target, Card
    MD_DRAW_COUNT,  // 修正摸牌阶段摸牌数
    MD_TARGET_VALIDATION,  // 修正是否为合法目标(boolean)。Player为目标。 Source, Card
}
