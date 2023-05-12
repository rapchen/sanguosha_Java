package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.player.Player;

import java.util.List;

/**
 * 闪电
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
public class Lightning extends DelayedTrickCard {
    public Lightning(Suit suit, Point point) {
        super(suit, point);
        name = "Lightning";
        nameZh = "闪电";
    }

    @Override
    public boolean canUseTo(Player source, Player target) {
        // TODO 这里是否是合法目标和出牌阶段是否可用还要拆开，例如顺手的距离判断、闪电的转移等
        return target == source && super.canUseTo(source, target);
    }

    @Override
    public void doDelayedEffect(Player target) {
        Judgement judge = target.doJudge(nameZh,
                card -> card.suit == Suit.SPADE && card.point.ge(Point.POINT_2) && Point.POINT_9.ge(card.point));
        if (judge.success) {
            target.engine.doDamage(null, target, 3);
            xFields.put("Lightning_Discard", true);  // 生效后加一个标记，用于置入弃牌堆
        }
    }

    @Override
    public void doAfterDelayedEffect(Player target) {
        if (xFields.containsKey("Lightning_Discard")) {  // 如果生效则置入弃牌堆
            target.engine.moveToDiscard(this);
        } else {  // 从下家开始依次遍历所有玩家，找到下一个合适的目标
            List<Player> players = target.getOtherPlayers();
            players.add(target);
            for (Player player : players) {
                if (super.canUseTo(null, player)) {  // TODO 应该改为是否合法目标
                    player.judgeArea.add(this);
                    return;
                }
            }
            target.engine.moveToDiscard(this);  // 没有合法目标，弃置
        }
    }
}
