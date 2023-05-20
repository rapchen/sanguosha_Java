package com.rapchen.sanguosha.core;

import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.Table;
import com.rapchen.sanguosha.core.data.UserTableVO;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.equip.EquipCard;
import com.rapchen.sanguosha.core.data.card.trick.DelayedTrickCard;
import com.rapchen.sanguosha.core.pack.StandardCards;
import com.rapchen.sanguosha.core.player.*;
import com.rapchen.sanguosha.core.skill.*;
import com.rapchen.sanguosha.exception.GameOverException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

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
    public List<Player> playersWithDead;  // 包括死亡武将在内的
    public Player currentPlayer;  // 当前回合角色
    public SkillManager skills;  // 技能管理器

    public Random random;

    public static final Engine eg = new Engine();  // 单例

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
        skills = new SkillManager();
        players.get(0).addSkill(BiYue.class);
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

    // 牌、牌堆相关

    private void initPile() {
        Card.nextCardId = 1;  // 重置Card的自增ID
        new StandardCards(this).init();
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
            for (Card card : cards) {
                card.place = Card.Place.DRAW;
            }
        }
        if (table.drawPile.isEmpty()) {
            throw new GameOverException("平局");
        }
        return table.drawPile.pollFirst();
    }

    /**
     * 从牌堆取多张牌
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
     * 将牌放入弃牌堆（不管移除，只管添加到弃牌堆）
     */
    public void moveToDiscard(Card card) {
        moveCard(card, Card.Place.DISCARD, null, "");
    }
    public void moveToDiscard(List<Card> cards) {
        moveCards(cards, Card.Place.DISCARD, null, "");
    }

    /**
     * 移动牌
     * @param card 移动的牌
     * @param targetPlace 目标位置
     * @param target 目标角色。如果无角色（如弃牌堆）则为null
     * @param reason 移动原因
     */
    public void moveCard(Card card, Card.Place targetPlace, Player target, String reason) {
        moveCards(Collections.singletonList(card), targetPlace, target, reason);
    }
    /**
     * 移动牌
     * @param cards 移动的牌
     * @param targetPlace 目标位置
     * @param target 目标角色。如果无角色（如弃牌堆）则为null
     * @param reason 移动原因
     */
    public void moveCards(List<Card> cards, Card.Place targetPlace, Player target, String reason) {
        // 移除牌逻辑
        for (Card card : cards) {
            switch (card.place) {
                case HAND, EQUIP, JUDGE -> {
                    card.owner.doRemoveCard(card);  // TODO 后面可以按位置拆，但是失去时机可以放一起
                } case DRAW -> {
                    table.drawPile.remove(card);
                } case DISCARD -> {
                    table.discardPile.remove(card);
                }
                // TODO 移除牌的逻辑
            }
            card.place = targetPlace;  // 变更卡牌位置
        }
        // 添加牌逻辑
        switch (targetPlace) {
            case DRAW -> {  // 加入摸牌堆。目前默认是放到牌堆顶
                for (int i = cards.size() - 1; i >= 0; i--) {
                    Card card = cards.get(i);
                    table.drawPile.addFirst(card);
                }
            } case DISCARD -> {
                table.discardPile.addAll(cards);
            } case HAND -> {
                target.handCards.addAll(cards);
            } case EQUIP -> {
                for (Card card : cards) {
                    target.equips.putEquip((EquipCard) card);
                }
            } case JUDGE -> {
                for (Card card : cards) {
                    target.judgeArea.add((DelayedTrickCard) card);
                }
            }
            // TODO 添加牌的逻辑
        }
        // 变更卡牌位置对应角色
        for (Card card : cards) {
            card.place = targetPlace;
            card.owner = target;
        }
    }

    // 角色相关

    /** 获取所有玩家，从当前回合角色开始 */
    public List<Player> getAllPlayers() {
        List<Player> result = new ArrayList<>();
        if (currentPlayer == null || !currentPlayer.alive) {
            result.addAll(players);
        } else {
            int idx = players.indexOf(currentPlayer);
            result.addAll(players.subList(idx, players.size()));
            result.addAll(players.subList(0, idx));
        }
        return result;
    }

    /**
     * 造成伤害。source可以是null
     */
    public void doDamage(Player source, Player target, int damageCount) {
        Damage damage = new Damage(source, target, damageCount);
        if (source != null) {  // 造成伤害时
            trigger(new Event(Timing.DAMAGE_DOING, source).withField("Damage", damage));
        }
        target.hp -= damageCount;
        if (source == null) {
            log.info("{} 受到了 {} 点伤害，无伤害来源", target, damageCount);
        } else {
            log.info("{} 对 {} 造成了 {} 点伤害", source, target, damageCount);
        }
        checkDeath(target);
        if (source != null) {  // 造成伤害后
            trigger(new Event(Timing.DAMAGE_DONE, source).withField("Damage", damage));
        }
    }

    /**
     * 判断角色是否死亡
     */
    private void checkDeath(Player player) {
        if (player.hp <= 0) {
            log.info("{} 陷入濒死！", player);
            player.callRescue();  // 求桃
            if (player.hp <= 0) {
                log.info("{} 死亡！", player);
                player.alive = false;
                players.remove(player);
                checkGameOver();
            }
        }
    }

    /**
     * 触发一个事件
     * @param event 事件
     */
    public void trigger(Event event) {
        skills.trigger(event);
    }

    /**
     * 触发一个修正（如攻击范围修改、距离修改）。以事件形式发送，返回修改后的值
     * @param event 事件
     * @param value 修改前的值
     */
    public int triggerModify(Event event, int value) {
        return skills.triggerModify(event, value);
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
            log.info(player.getDetail(true));
        }
    }
}
