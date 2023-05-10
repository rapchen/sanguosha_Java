package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import lombok.extern.slf4j.Slf4j;

import java.util.InputMismatchException;
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
        return chooseCard(cards, false, "请使用一张牌，0结束：", "choosePlayCard");
    }

    @Override
    protected Card chooseDiscard(Player target, List<Card> cards) {
        return chooseCard(cards, true, "请弃置一张手牌：", "chooseDiscard");
    }

    /**
     * 要求用户选一张牌
     * @param cards  可选牌的列表
     * @param forced 是否必须选择
     * @param prompt 给用户的提示语
     * @param reason 选牌原因，通常给AI做判断用
     * @return 选择的牌。如果不选，就返回null。
     */
    @Override
    public Card chooseCard(List<Card> cards, boolean forced, String prompt, String reason) {
        log.warn(prompt);  // TODO 目前打给用户的都用WARN，后台的用INFO。之后可以打到不同的输出
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
            try {
                chosen = sc.nextInt();
            } catch (InputMismatchException e) {
                log.warn("请重新选择：");
                continue;
            }
            if (chosen >= 1 && chosen <= max) break;  // 选择完成
            if (chosen == 0 && !forced) break;  // 跳过选择
            if (chosen == -1) printTable();  // 打印桌面
            if (chosen == -2) engine.printTable();  // 打印桌面（Debug模式）
            else log.warn("请重新选择：");
            // TODO 这里可以把提示文字重新打一遍
        }
        return chosen;
    }
}
