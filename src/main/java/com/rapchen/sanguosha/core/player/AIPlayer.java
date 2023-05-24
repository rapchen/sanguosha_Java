package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.trick.Nullification;
import com.rapchen.sanguosha.core.general.General;

import java.util.List;

/**
 * @author Chen Runwen
 * @time 2023/4/24 18:10
 */
public class AIPlayer extends Player {
    public AIPlayer(Engine engine, int id, String name) {
        super(engine, id, name);
    }

    @Override
    protected Card choosePlayCard(List<Card> cards) {
        if (cards.isEmpty()) return null;
        return cards.get(0);
    }

    @Override
    protected Card chooseDiscard(Player target, List<Card> cards) {
        if (cards.isEmpty()) return null;
        return cards.get(0);
    }

    @Override
    public <T extends Card> T chooseCard(List<T> cards, boolean forced, String prompt, String reason) {
        if (cards.isEmpty()) return null;
        switch (reason) {
            case "askForDodge", "askForSlash" -> {  // 要求出杀闪：总是出
                return cards.get(0);
            } case "askForPeach" -> {  // 求桃：只给自己
                Player target = (Player) xFields.get("askForPeach_Target");
                return target == this ? cards.get(0) : null;
            } case "askForNullification" -> {  // 要求无懈：如果对我有坏处（或者对别人有好处），就用无懈
                CardEffect effect = (CardEffect) xFields.get("askForNulli_CardEffect");
                if (effect == null) return null;
                if (calcBenefit(effect) < 0)
                    return cards.get(0);
                return null;
            } case "askForCardFromPlayer" -> {  // 要求一角色处的一张牌：目前默认是坏事，别人的总是选，自己的尽量不选
                String reason1 = (String) xFields.get("askForCardFromPlayer_Reason");
                Player target = (Player) xFields.get("askForCardFromPlayer_Target");
                if (target == this) return forced ? cards.get(0) : null;
                else return cards.get(0);
            } case "KylinBowSkill" -> {  // 麒麟弓：弃
                return cards.get(0);
            } default -> {  // 默认逻辑：必须选就选一张，否则放弃
                return forced ? cards.get(0) : null;
            }
        }
        // return cards.get(0);
    }

    @Override
    public <T extends General> T chooseGeneral(List<T> generals, boolean forced, String prompt, String reason) {
        return generals.get(0);
    }

    /**
     * 判断一张卡牌的使用是否对我有益。越大越有益，0为无关，负数有害
     */
    private int calcBenefit(CardEffect effect) {
        Card card = effect.getCard();
        if (card instanceof Nullification nulli) {  // 如果是无懈，则与无懈的目标相反
            if (nulli.targetEffect == null) return 0;
            else return -calcBenefit(nulli.targetEffect);
        }
        // 如果有目标，看目标是谁，如果是对面，则与牌原本的有益性相反
        if (effect.target == null) return 0;
        else return effect.getCard().benefit * (effect.target == this ? 1 : -1);
    }

    @Override
    protected int chooseChoice(List<String> choices, boolean forced, String prompt, String reason) {
        if (choices.isEmpty()) return 1;
        switch (reason) {
            case "DoubleSwordSkill" -> {  // 雌雄，总是发动
                return 1;
            } case "EightDiagramSkill" -> {  // 八卦，总是发动
                return 1;
            } default -> {  // 默认逻辑：必须选就选1，否则放弃
                return forced ? 1 : 0;
            }
        }
    }

    @Override
    protected int chooseNumber(int max, boolean forced) {
        return 1;
    }

}
