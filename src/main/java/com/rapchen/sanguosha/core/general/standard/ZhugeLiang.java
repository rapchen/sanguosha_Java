package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.data.card.trick.Duel;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Phase;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * 诸葛亮
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class ZhugeLiang extends General {
    public ZhugeLiang() {
        super("ZhugeLiang", "诸葛亮", Gender.MALE, Nation.SHU, 3);
        skills.add(GuanXing.class);
        skills.add(KongCheng.class);
    }

    // 观星: 准备阶段开始时，你可以观看牌堆顶的X张牌，然后将任意数量的牌置于牌堆顶，将其余的牌置于牌堆底。（X为存活角色数且至多为5）
    public static class GuanXing extends TriggerSkill {
        public GuanXing() {
            super("GuanXing", "观星", new Timing[]{Timing.PHASE_BEGIN});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final Phase phase = (Phase) event.xFields.get("Phase");
            if (phase != Phase.PHASE_PREPARE) return;
            if (askForUse(owner)) {
                int X = Math.min(Engine.eg.players.size(), 5);
                List<Card> cards = Engine.eg.getCardsFromDrawPile(X);
                Deque<Card> drawPile = Engine.eg.table.drawPile;
                // 牌堆顶
                CardChoose choose = new CardChoose(owner).in(cards)
                        .count(cards.size(), 0)
                        .reason(name, "你正在发动【观星】，请按顺序选择放到牌堆顶的牌，0放弃");
                List<Card> topCards = choose.choose();
                List<Card> bottomCards = new ArrayList<>();
                // 牌堆底
                if (!choose.candidates.isEmpty()) {
                    CardChoose choose2 = new CardChoose(owner).in(choose.candidates)
                            .count(choose.candidates.size(), 0)
                            .reason(name, "你正在发动【观星】，请按顺序选择放到牌堆底的牌，0放弃（剩余牌将按原顺序放回原牌堆顶）");
                    bottomCards = choose2.choose();
                    topCards.addAll(choose2.candidates);  // 剩余牌
                }
                // 放牌
                String log = "将 ";
                if (!topCards.isEmpty()) log += Card.cardsToString(topCards) + " 置于牌堆顶 ";
                if (!bottomCards.isEmpty()) log += Card.cardsToString(bottomCards) + " 置于牌堆底 ";
                doLog(log);

                Collections.reverse(topCards);
                for (Card card : topCards) {
                    drawPile.addFirst(card);
                }
                Collections.reverse(bottomCards);
                drawPile.addAll(bottomCards);
            }
        }
    }

    // 空城: 锁定技，若你没有手牌，你不能被选择为【杀】或【决斗】的目标。 
    public static class KongCheng extends TriggerSkill {
        public KongCheng() {
            super("KongCheng", "空城", new Timing[]{Timing.MD_TARGET_VALIDATION});
            compulsory = true;
        }

        @Override
        public int onModify(Event event, int value) {
            final Card card = (Card) event.xFields.get("Card");
            if (owner.getCardCount("h") == 0
                    && (card instanceof Slash || card instanceof Duel))
                return 0;
            return value;
        }
    }

}
