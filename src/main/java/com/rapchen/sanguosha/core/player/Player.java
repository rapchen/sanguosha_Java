package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.Dodge;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色对象。可以是人或AI
 *
 * @author Chen Runwen
 * @time 2023/4/24 18:04
 */
@Slf4j
public abstract class Player {
    public final Engine engine;

    public int id;
    public String name;
    public boolean alive = true;
    public int hp;
    public int maxHp;
    public List<Card> handCards;

    public Player(Engine engine, int id, String name) {
        this.engine = engine;
        this.id = id;
        this.name = name;
        this.handCards = new ArrayList<>();
        // TODO 武将
        this.hp = 4;
        this.maxHp = 4;
    }

    /**
     * 进行一个回合
     */
    public void doTurn() {
        // TODO 回合开始时机
        doPreparePhase();
        doJudgePhase();
        doDrawPhase();
        doPlayPhase();
        doDiscardPhase();
        doEndPhase();
        // TODO 回合结束时机
    }

    /* =============== begin 阶段 ================ */

    /**
     * 准备阶段。 TODO 各个阶段的开始结束共同逻辑可以用AOP，考虑整合SpringAOP
     */
    private void doPreparePhase() {
    }

    /**
     * 判定阶段
     */
    private void doJudgePhase() {
    }

    /**
     * 摸牌阶段
     */
    private void doDrawPhase() {
        this.drawCards(2);
    }

    /**
     * 出牌阶段
     */
    private void doPlayPhase() {
        while (true) {
            if (!askForPlayCard()) break;
        }
    }

    /**
     * 弃牌阶段
     */
    private void doDiscardPhase() {
        if (handCards.size() > hp) {
            askForDiscard(handCards.size() - hp);
        }
    }

    /**
     * 结束阶段
     */
    private void doEndPhase() {
    }

    /* =============== end 阶段 ================ */

    /* =============== begin 功能执行 ================ */

    public void drawCards(int count) {
        List<Card> cards = engine.getCardsFromDrawPile(count);
        handCards.addAll(cards);
        log.info("{} 摸了{}张牌：{}", this.name, cards.size(), Card.cardsToString(cards));
        // TODO 获得牌事件：只触发一次就行
    }

    /**
     * 弃牌。目前只是弃自己的牌
     * @param card 牌
     */
    protected void doDiscard(Card card) {
//        if (!handCards.contains(card)) {
//            throw new CardPlaceException(
//                    String.format("弃牌不是自己的牌，player:%s, card:%s", this, card));
//        }
        handCards.remove(card);
        engine.table.discardPile.addLast(card);
    }
    protected void doDiscard(List<Card> cards) {
        engine.table.discardPile.addAll(cards);
    }

    /**
     * 使用牌 TODO 加上使用目标
     * @param card
     */
    protected void useCard(Card card) {
        card.doUse(this, getOtherPlayers());
    }

    /**
     * 获取其他玩家
     */
    private List<Player> getOtherPlayers() {
        List<Player> players = new ArrayList<>(engine.players);
        players.remove(this);
        return players;
    }

    /**
     * 玩家视角的打印桌面
     */
    protected void printTable() {
        log.warn(engine.table.printForPlayer());
        log.warn(getDetail());
        for (Player player : engine.players) {
            if (player == this) continue;
            log.warn(player.getDetailForOthers());
        }
    }

    @Override
    public String toString() {
        return "Player " + id + '(' + name + ")";
    }

    public String getDetail() {
        return "Player " + id + '(' + name +
                ") HP " + hp + '/' + maxHp +
                "\n手牌: " + Card.cardsToString(handCards);
    }

    public String getDetailForOthers() {
        return "Player " + id + '(' + name +
                ") HP " + hp + '/' + maxHp +
                ", 手牌: " + handCards.size();
    }

    public static String playersToString(List<Player> players) {
        return playersToString(players, false);
    }

    public static String playersToString(List<Player> players, boolean withNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (withNumber) {
                sb.append(i + 1).append(": ");
            }
            sb.append(player);
            if (i < players.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    public void doDamage(Player target, int damageCount) {
        engine.doDamage(this, target, damageCount);
    }

    /* =============== end 功能执行 ================ */

    /* =============== begin 要求玩家操作的方法，通常是abstract ================ */

    /**
     * 玩家出牌。这里是通用逻辑，具体策略交给askForPlayCard
     * @return 是否出牌。false时出牌结束
     */
    public boolean askForPlayCard() {
        // 先找出可以使用的牌 TODO 后面用canUse
        List<Card> cards = handCards.stream().filter(card -> !(card instanceof Dodge)).toList();
        Card card = choosePlayCard(cards);
        if (card != null) useCard(card);
        // TODO 选择了牌还需要选目标
        return card != null;
    }
    protected abstract Card choosePlayCard(List<Card> cards);

    /**
     * 要求弃牌  TODO 非强制弃牌
     * @param count 弃牌数量
     */
    public boolean askForDiscard(int count) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card card = chooseDiscard();
            cards.add(card);
            handCards.remove(card);  // TODO 这里弃牌时机应该是一起发生
        }
        doDiscard(cards);
        log.info("{} 弃了{}张牌：{}", this.name, cards.size(), Card.cardsToString(cards));
        return true;
    }
    protected abstract Card chooseDiscard();

    /**
     * 要求用户选一张牌
     * @param cards 可选牌的列表
     * @param prompt 给用户的提示语
     * @param forced 是否必须选择
     * @return 选择的牌。如果不选，就返回null。
     */
    protected abstract Card chooseCard(List<Card> cards, String prompt, boolean forced);

    /**
     * 要求用户选一个数 [1,max]。 -1可以调出查看界面，目前只是打印牌桌
     * @param forced 是否必须选择，非必选的话可以用0跳过
     */
    protected abstract int chooseNumber(int max, boolean forced);

    public boolean askForDodge() {
        List<Card> dodges = handCards.stream().filter(card -> card instanceof Dodge).toList();
        Card card = chooseCard(dodges, "请使用一张闪，0放弃：", false);
        if (card != null) useCard(card);
        return card != null;
    }
}
