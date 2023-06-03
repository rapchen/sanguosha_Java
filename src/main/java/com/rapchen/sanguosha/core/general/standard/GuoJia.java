package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.*;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.*;

import java.util.List;

/**
 * 郭嘉
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class GuoJia extends General {
    public GuoJia() {
        super("GuoJia", "郭嘉", Gender.MALE, Nation.WEI, 3);
        skills.add(TianDu.class);
        skills.add(YiJi.class);
    }

    // 天妒: 每当你的判定牌生效后，你可以获得之。
    public static class TianDu extends TriggerSkill {
        public TianDu() {
            super("TianDu", "天妒", new Timing[]{Timing.JUDGE_DONE});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final Judgement judge = (Judgement) event.xFields.get("Judge");
            if (askForUse(owner)) {
                doLog("获得了判定牌 %s", judge.card);
            }
            owner.obtain(judge.card, name);
        }
    }

    // 遗计: 每当你受到1点伤害后，你可以观看牌堆顶的两张牌，然后将这两张牌任意分配。
    public static class YiJi extends TriggerSkill {
        public YiJi() {
            super("YiJi", "遗计", new Timing[]{Timing.DAMAGED_DONE});
            useByDefault = true;
            setTransSkill(new YiJiTrans());
        }

        @Override
        public void onTrigger(Event event) {
            final Damage damage = (Damage) event.xFields.get("Damage");
            for (int i = 0; i < damage.count; i++) {
                if (askForUse(owner)) {
                    List<Card> cards = Engine.eg.getCardsFromDrawPile(2);
                    // 发牌给其他角色
                    try (Fields.TmpField tf = owner.xFields.tmpField("YiJi_Cards", cards)) {
                        boolean succ = askForTransform();
                        if (succ && !cards.isEmpty()) askForTransform();
                    }
                    // 剩余牌给自己
                    if (!cards.isEmpty()) {
                        doLog(String.format("获得了 %s", Card.cardsToString(cards)));
                        owner.obtain(cards, name);
                    }
                }
            }
        }
    }

    public static class YiJiTrans extends TriggeredTransformSkill {
        public YiJiTrans() {
            super(YiJiCard.class);
            maxCardCount = 2;
        }

        @Override
        public List<Card> extraCards() {
            return (List<Card>) owner.xFields.get("YiJi_Cards");
        }

        @Override
        public boolean cardFilter(Card card) {
            return ((List<Card>) owner.xFields.get("YiJi_Cards")).contains(card);
        }

        @Override
        public Card serveAs() {
            if (chosenCards.isEmpty()) return null;
            return Card.createVirtualCard(YiJiCard.class, chosenCards);
        }
    }

    public static class YiJiCard extends SkillCard {
        public YiJiCard() {
            willThrow = false;
        }

        @Override
        public void doEffect(CardEffect effect) {
            Player source = effect.getSource(), target = effect.target;
            List<Card> cards = effect.getCard().subCards;
            skill.doLog("将 %s 交给了 %s", Card.cardsToString(cards), target);
            target.obtain(cards, name);
            ((List<Card>) source.xFields.get("YiJi_Cards")).removeAll(cards);
        }
    }
}
