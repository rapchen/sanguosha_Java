package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

/**
 * 玩家角色。目前是通过stdin进行交互
 * @author Chen Runwen
 * @time 2023/4/24 18:10
 */
@Slf4j
public class UserPlayer extends Player {
    public UserPlayer(Engine engine, int id, String name) {
        super(engine, id, name);
    }

    @Override
    protected Card choosePlayCard(List<Card> cards) {
        return chooseCard(cards, "请使用一张牌，0结束：", false);
    }

    @Override
    protected Card chooseDiscard() {
        return chooseCard(handCards, "请弃置一张手牌：", true);
    }

//    @Override
//    public boolean askForDodge() {
//        List<Card> dodges = handCards.stream().filter(card -> card instanceof Dodge).toList();
//        Card card = askForChooseCard(dodges, "请使用一张闪，0放弃：", false);
//        if (card != null) useCard(card);
//        return card != null;
//    }

    /**
     * 要求用户选一张牌
     * @param cards 可选牌的列表
     * @param prompt 给用户的提示语
     * @param forced 是否必须选择
     * @return 选择的牌。如果不选，就返回null。
     */
    @Override
    protected Card chooseCard(List<Card> cards, String prompt, boolean forced) {
        log.warn(prompt);  // TODO 目前打给用户的都用WARN，后台的用INFO
        log.warn(Card.cardsToString(cards, true));
        int chosen = chooseNumber(cards.size(), forced);
        return chosen == 0 ? null : cards.get(chosen - 1);
    }

    /**
     * 要求用户选一个数 [1,max]。 -1可以调出查看界面，目前只是打印牌桌。-2是debug
     * @param forced 是否必须选择，非必选的话可以用0跳过
     */
    @Override
    protected int chooseNumber(int max, boolean forced) {
        Scanner sc = new Scanner(System.in);
        int chosen = 0;
        while (true) {
            chosen = sc.nextInt();
            if (chosen >= 1 && chosen <= max) break;
            if (chosen == 0 && !forced) break;  // 跳过选择
            if (chosen == -1) printTable();
            if (chosen == -2) engine.printTable();
            else log.warn("请重新选择：");
            // TODO 这里可以把提示文字重新打一遍
        }
        return chosen;
    }
}
