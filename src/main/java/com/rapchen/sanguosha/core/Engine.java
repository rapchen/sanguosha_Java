package com.rapchen.sanguosha.core;

import com.rapchen.sanguosha.core.data.Table;
import com.rapchen.sanguosha.core.data.UserTableVO;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.pack.StandardCards;
import com.rapchen.sanguosha.core.player.*;
import com.rapchen.sanguosha.exception.GameOverException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 单例Engine，实际控制游戏流程
 * @author Chen Runwen
 * @time 2023/4/14 12:29
 */
@Component
@Slf4j
public class Engine {

    public Table table;
    public List<Player> players;
    // 包括死亡武将在内的
    public List<Player> playersWithDead;

    public Random random;

    public void gameStart() {
        // 准备工作
        random = new Random();
        // 创建table
        table = new Table();
        // 初始化角色 TODO 多人局
        players = new ArrayList<>();
        players.add(new UserPlayer(this, 0, "user"));
        players.add(new AIPlayer(this, 1,"AI"));
        playersWithDead = new ArrayList<>(players);
        // TODO 选将
        // players.get(1).chooseGeneral();
        // 初始化牌堆
        initPile();
        // 游戏开始
        // 初始手牌
        for (Player player : players) {
            player.drawCards(4);
        }
        // 轮流回合
        try {
            while (true) {
                doOneRound();
            }
        } catch (GameOverException e) {
            log.warn(e.getMessage());
            log.warn("========================================================");
            gameStart();
        }
    }

    /**
     * 一轮。各个玩家轮流进行回合 TODO 轮开始时机
     */
    private void doOneRound() {
        for (Player player : players) {
            player.doTurn();
        }
    }

    private void initPile() {
        Card.nextCardId = 1;  // 重置Card的自增ID
        new StandardCards(this).init();
//        int cardId = 1;
//        List<Card> cards = new ArrayList<>();
//        for (Card.Suit suit : Card.Suit.REGULAR_SUITS) {
//            for (Card.Point point : Card.Point.REGULAR_POINTS) {
//                cards.add(new Slash(suit, point, cardId++));
//            }
//        }
        // 洗牌
        List<Card> cards = new ArrayList<>(table.drawPile);
        Collections.shuffle(cards);
        table.drawPile.clear();
        table.drawPile.addAll(cards);
        // TODO 可配置的扩展包
    }

    /**
     * 从牌堆取牌
     */
    public Card getCardFromDrawPile() {
        if (table.drawPile.isEmpty()) {  // 洗牌
            List<Card> cards = new ArrayList<>(table.discardPile);
            Collections.shuffle(cards);
            table.drawPile.addAll(cards);
            table.discardPile.clear();
        }
        if (table.drawPile.isEmpty()) {
            throw new GameOverException("平局");
        }
        return table.drawPile.pollFirst();
    }

    /**
     * 从牌堆取牌
     * @param count 张数
     */
    public List<Card> getCardsFromDrawPile(int count) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card card = getCardFromDrawPile();
            cards.add(card);
        }
        return cards;
    }

    /**
     * 造成伤害
     */
    public void doDamage(Player source, Player target, int damageCount) {
        target.hp -= damageCount;
        log.info("{} 对 {} 造成了 {} 点伤害", source, target, damageCount);
        checkDeath(target);
    }

    /**
     * 判断角色是否死亡
     * @param player
     */
    private void checkDeath(Player player) {
        if (player.hp <= 0) {
            log.info("{} 陷入濒死！", player);
            // TODO 濒死求桃
            log.info("{} 死亡！", player);
            player.alive = false;
            players.remove(player);
            checkGameOver();
        }
    }

    /**
     * 判断游戏是否结束 TODO 多人局
     */
    private void checkGameOver() {
        if (players.size() <= 1) {
            throw new GameOverException(String.format("玩家 %s 获胜！", players.get(0)));
        }
    }

    /**
     * 返回UserTableVO，即用户界面数据
     */
    public UserTableVO getUserTableVO() {
        printTable();
        UserTableVO result = new UserTableVO();
        result.setDrawPileCount(table.drawPile.size());
        result.setDiscardPileCount(table.discardPile.size());
        result.setUserPlayer(new UserPlayerVO(players.get(0)));
        result.setPlayers(Collections.singletonList(new PlayerVO(players.get(1))));
        return result;
    }

    public void printTable() {
        log.info(table.print());
        for (Player player : players) {
            log.info(player.getDetail());
        }
    }
}
