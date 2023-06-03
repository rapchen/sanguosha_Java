package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 技能牌
 * @author Chen Runwen
 * @time 2023/5/5 18:43
 */
@Slf4j
public abstract class SkillCard extends Card {
    public Event event = null;  // 关联对应触发技的Event

    public SkillCard(Suit suit, Point point) {
        super(suit, point);
        subType = SubType.SKILL;
        name = this.getClass().getSimpleName();
    }

    public SkillCard() {
        this(Suit.SUIT_NO, Point.POINT_NO);
    }

    @Override
    public void doUseLog(Player source, List<Player> targets) {
        if (targets.isEmpty()) {
            skill.doLog();
        } else {
            skill.doLog(String.format("目标是 %s", Player.playersToString(targets)));
        }
    }

    @Override
    public String toString() {
        return nameZh + "牌[" + suit + point + "]";
    }
}
