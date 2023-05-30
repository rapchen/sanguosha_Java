package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Utils;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.Table;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.general.General;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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
        return chooseCard(new CardChoose(this, cards, false,
                "choosePlayCard", "请使用一张牌，0结束："));
    }

    /**
     * 要求用户选一张牌
     * @param choose 卡牌选择对象
     * @return 选择的牌。如果不选，就返回null。
     */
    @Override
    public Card chooseCard(CardChoose choose) {
        List<Card> cards = choose.candidates;
        while (true) {
            log.warn(choose.prompt);  // TODO 目前打给用户的都用WARN，后台的用INFO。之后可以打到不同的输出
            log.warn(Card.cardsToString(cards, true));
            int chosen = chooseNumber(cards.size(), choose.forced);
            if (chosen >= 0) {
                return chosen == 0 ? null : cards.get(chosen - 1);
            }
            log.warn("请重新选择：");  // 返回<0的结果就重新询问
        }
    }

    /**
     * 要求用户选一个武将
     */
    @Override
    public <T extends General> T chooseGeneral(List<T> generals, boolean forced, String prompt, String reason) {
        while (true) {
            log.warn(prompt);
            log.warn(Utils.objectsToString(generals, true));
            int chosen = chooseNumber(generals.size(), forced);
            if (chosen >= 0) {
                return chosen == 0 ? null : generals.get(chosen - 1);
            }
            log.warn("请重新选择：");  // 返回<0的结果就重新询问
        }
    }

    /**
     * 要求用户做一个选择
     * @param choices 选项的列表
     * @param forced  是否必须选择
     * @param prompt  给用户的提示语
     * @param reason  选择原因，通常给AI做判断用
     * @return 选择的选项序号。如果不选，就返回null。
     */
    @Override
    protected int chooseChoice(List<String> choices, boolean forced, String prompt, String reason) {
        while (true) {
            log.warn(prompt);
            int size = choices.size();  // 打印选项列表
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append(i + 1).append(": ").append(choices.get(i));
                if (i < size - 1 || !forced) sb.append(", ");
            }
            if (!forced) sb.append("0: 取消");
            log.warn(sb.toString());

            int chosen = chooseNumber(choices.size(), forced);
            if (chosen >= 0) {
                return chosen;
            }
            log.warn("请重新选择：");  // 返回<0的结果就重新询问
        }
    }

    /**
     * 要求用户选一个数 [1,max]。
     * 用户输入[1,max]时直接返回，输入0时如果可跳过则返回0，否则返回-1。
     * 输入-1可以调出查看界面，目前只是打印牌桌。-2是debug。-3~-6是作弊。注意，这些结果都会返回-1。
     * @param forced 是否必须选择，非必选的话可以用0跳过
     * @return 合法且>=0的输入会直接返回。所有其他输入会返回-1，意味需要重新输入。
     */
    @Override
    protected int chooseNumber(int max, boolean forced) {
        Scanner sc = new Scanner(System.in);
        int chosen = 0;
        try {
            chosen = sc.nextInt();
        } catch (InputMismatchException e) {
            log.warn("输入非法");
            return -1;
        }
        if (chosen >= 1 && chosen <= max) return chosen;  // 选择完成
        if (chosen == 0 && !forced) return chosen;  // 跳过选择

        if (chosen == -1) printTable();  // 打印桌面
        if (chosen == -2) engine.printTable();  // 打印桌面（Debug模式）
        if (chosen == -3) cheatGetCard(this);  // 作弊，给自己牌
        if (chosen == -4) cheatGetCard(getOtherPlayers().get(0));  // 作弊，给对面牌
        if (chosen == -5) cheatDamage(this, getOtherPlayers().get(0));  // 作弊，打伤害
        if (chosen == -6) cheatDamage(getOtherPlayers().get(0), this);  // 作弊，打伤害
        return -1;  // 失败返回-1
    }

    /**
     * 作弊发牌，从摸牌/弃牌堆里发
     * @param player 牌给谁
     */
    private void cheatGetCard(Player player) {
        Table table = engine.table;
        List<Card> cards = new ArrayList<>(table.drawPile);
        cards.addAll(table.discardPile);
        Card card = chooseCard(new CardChoose(this, cards, false,
                "cheatGetCard", "选择你要的牌："));
        if (card != null) {
            table.drawPile.remove(card);
            table.discardPile.remove(card);
            player.handCards.add(card);
            card.place = player.HAND;
        }
    }

    /** 作弊伤害 */
    private void cheatDamage(Player source, Player target) {
        int damageCnt = chooseNumber(10000, false);
        engine.doDamage(new Damage(source, target, damageCnt, "cheatDamage"));
    }
}
