package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.card.*;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TransformSkill;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

import java.util.Map;

/**
 * 孙尚香
 * @author Chen Runwen
 * @time 2023/6/3 0:47
 */
public class SunShangxiang extends General {
    public SunShangxiang() {
        super("SunShangxiang", "孙尚香", Gender.FEMALE, Nation.WU, 3);
        skills.add(JieYin.class);
        skills.add(XiaoJi.class);
    }

    // 结姻: 出牌阶段限一次，你可以弃置两张手牌并选择一名已受伤的男性角色：若如此做，你和该角色各回复1点体力。
    public static class JieYin extends TransformSkill {
        public JieYin() {
            super("JieYin", "结姻");
            maxCardCount = 2;
        }

        @Override
        public boolean cardFilter(Card card) {
            return card.place.isHand();
        }

        @Override
        public Card serveAs() {
            if (chosenCards.size() != 2) return null;
            return Card.createVirtualCard(JieYinCard.class, chosenCards);
        }
    }

    public static class JieYinCard extends SkillCard {
        @Override
        public boolean canUseTo(Player source, Player target) {
            return target.gender == Gender.MALE && target.injured();
        }

        @Override
        public void doEffect(CardEffect effect) {
            Player source = effect.getSource(), target = effect.target;
            source.doRecover(1);
            target.doRecover(1);
        }
    }

    // 枭姬: 每当你失去一张装备区的装备牌后，你可以摸两张牌。
    public static class XiaoJi extends TriggerSkill {
        public XiaoJi() {
            super("XiaoJi", "枭姬", new Timing[]{Timing.CARD_MOVED});
            onlyOwner = false;
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final CardMove move = (CardMove) event.xFields.get("CardMove");
            for (Map.Entry<Card, Place> entry : move.cardsPlace.entrySet()) {
                if (entry.getValue() == owner.EQUIP && askForUse(owner)) {
                    doLog();
                    owner.drawCards(2);
                }
            }
        }
    }
}
