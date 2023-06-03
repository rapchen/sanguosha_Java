package com.rapchen.sanguosha.core.data;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.Place;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Skill;
import com.rapchen.sanguosha.core.skill.Timing;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * 判定结果对象
 * @author Chen Runwen
 * @time 2023/5/11 21:53
 */
@Slf4j
public class Judgement {
    public Function<Card, Boolean> judgeFunc;  // 判定成功的条件。
    public Player player;  // 判定人
    public String reason;  // 判定原因
    public String nameZh;  // 判定中文名称

    // 判定结果
    public Card card;  // 判定牌
    public Boolean success;  // 是否判定成功。为null代表无好坏

    public Judgement(Card card, Boolean success) {
        this.card = card;
        this.success = success;
    }

    public Judgement(Player player, String nameZh, Function<Card, Boolean> judgeFunc) {
        this.player = player;
        this.nameZh = nameZh;
        this.judgeFunc = judgeFunc;
    }

    public Judgement judge() {
        Card card = Engine.eg.getCardFromDrawPile();
        setCard(card);
        log.warn("{} 因 {} 翻开了判定牌 {}", player, nameZh, card);
        // 判定牌生效前时机。可以改判
        Engine.eg.trigger(new Event(Timing.JUDGE_BEFORE_EFFECT, player).withField("Judge", this));

        String successStr = (success == null) ? "生效" : (success ? "成功" : "失败");
        log.warn("{} 的 {} 判定 {}，结果为 {}", player, nameZh, successStr, card);

        // 判定牌生效后时机。天妒
        Engine.eg.trigger(new Event(Timing.JUDGE_DONE, player).withField("Judge", this));
        // 仍在处理区的进入弃牌堆
        Engine.eg.moveToDiscard(card, Place.PlaceType.JUDGE_CARD);
        return this;
    }

    private void setCard(Card card) {
        Engine.eg.moveCard(card, player.JUDGE_CARD, "judge");
        this.card = card;
        this.success = judgeFunc.apply(card);
    }

    /**
     * 指定一张牌作为判定结果。通常指改判
     * @param newCard 指定的牌
     * @param skill 改判来源技能
     * @param swap 是否是替换原判定牌。如果是则旧判定牌进入手牌，否则进入弃牌堆
     */
    public Judgement rejudge(Card newCard, Skill skill, boolean swap) {
        // 新牌进入处理区
        Engine.eg.moveCard(newCard, Place.HANDLE, "judge");
        // 处理旧牌。
        if (swap) {
            skill.owner.obtain(this.card, skill.name);
        } else {
            Engine.eg.moveToDiscard(this.card);
        }
        // 新牌设置为判定牌
        setCard(newCard);
        return this;
    }
}
