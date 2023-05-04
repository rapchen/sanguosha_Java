package com.rapchen.sanguosha.core;

import com.rapchen.sanguosha.core.data.Table;
import com.rapchen.sanguosha.core.data.UserTableVO;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.Slash;
import com.rapchen.sanguosha.core.player.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void gameStart() {
        // 创建table
        table = new Table();
        // 初始化角色
        players = new ArrayList<>();
        players.add(new UserPlayer(this, 0, "user"));
        players.add(new AIPlayer(this, 1,"AI"));
        // 选将 TODO
        // players.get(1).chooseGeneral();
        // 初始化牌堆
        initPile();
        // 游戏开始
        for (Player player : players) {
            player.drawCards(4);
        }
    }

    private void initPile() {
        int cardId = 1;
        List<Card> cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.REGULAR_SUITS) {
            for (Card.Point point : Card.Point.REGULAR_POINTS) {
                cards.add(new Slash(suit, point, cardId++));
            }
        }
        Collections.shuffle(cards);
        table.drawPile.addAll(cards);
        // TODO 可配置的扩展包
    }

    public Card getCardFromDrawPile() {
        if (table.drawPile.isEmpty()) {  // 洗牌
            List<Card> cards = new ArrayList<>(table.discardPile);
            Collections.shuffle(cards);
            table.drawPile.addAll(cards);
            table.discardPile.clear();
        }
        return table.drawPile.pollFirst();
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

    private void printTable() {
        log.info(table.print());
        for (Player player : players) {
            log.info(player.toString());
        }
    }

}
