package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.Dodge;
import com.rapchen.sanguosha.core.data.card.FakeCard;
import com.rapchen.sanguosha.core.data.card.Slash;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    public int slashTimes = 1;

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
        slashTimes = 1;
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
     * 使用牌
     */
    protected void useCard(Card card, List<Player> targets) {
        card.doUse(this, targets);
    }

    /**
     * 打出牌
     */
    protected void responseCard(Card card, List<Player> targets) {
        card.doResponse(this, targets);
    }

    /**
     * 获取其他玩家
     */
    public List<Player> getOtherPlayers() {
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
        List<Card> cards = handCards.stream().filter(card -> card.canUseInPlayPhase(this)).toList();
        Card card = choosePlayCard(cards);
        if (card != null) {
            List<Player> targets = chooseTargets(card);
            useCard(card, targets);
        }
        return card != null;
    }

    /**
     * 选择牌的目标 TODO 现在自动选目标，后面要改成手动选，每张卡牌有一个方法返回合法目标
     * @param card 使用的牌
     */
    private List<Player> chooseTargets(Card card) {
        return switch (card.getName()) {
            case "Slash" -> getOtherPlayers();
            case "Dismantlement" -> getOtherPlayers();
            case "Snatch" -> getOtherPlayers();
            case "ArchersAttack" -> getOtherPlayers();
            case "BarbarianInvasion" -> getOtherPlayers();
            case "Dodge" -> new ArrayList<>();
            case "ExNihilo" -> Collections.singletonList(this);
            default -> new ArrayList<>();
        };
    }

    protected abstract Card choosePlayCard(List<Card> cards);

    /**
     * 弃自己的牌
     */
    public boolean askForDiscard(int count) {
        return askForDiscard(count, this);
    }
    /**
     * 要求弃牌  TODO 非强制弃牌
     * @param count  弃牌数量
     * @param target 弃牌目标
     */
    public boolean askForDiscard(int count, Player target) {
        List<Card> discards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<Card> choices = new ArrayList<>();
            if (target == this) {  // 弃自己牌
                choices.addAll(handCards); // TODO 弃装备牌
            } else {  // 弃他人牌
                if (target.handCards.size() > 0) {  // 所有手牌作为一个选项，随机弃一张
                    Card hCards = new FakeCard( target.handCards.size() + "张手牌");
                    hCards.addSubCards(target.handCards);
                    choices.add(hCards);
                }
            }
            if (!choices.isEmpty()) {
                Card card = chooseDiscard(target, choices);
                if (card.isVirtual()) {
                    int num = engine.random.nextInt(card.subCards.size());
                    card = card.subCards.get(num);
                }
                discards.add(card);
                target.handCards.remove(card);  // TODO 这里弃牌时机应该是一起发生
            }
        }
        doDiscard(discards);
        log.info("{} 弃了 {} {}张牌：{}", this.name, target.name, discards.size(), Card.cardsToString(discards));
        return true;
    }


    protected Card chooseDiscard(Player target) {
        return chooseDiscard(target, target.handCards);
    }
    protected abstract Card chooseDiscard(Player target, List<Card> cards);

    /**
     * 要求角色从目标角色的牌中选一张牌
     * @param target 目标角色
     * @param prompt 给用户的提示语
     * @param forced 是否必须选择
     * @return 选择的牌。如果不选或无牌可选，就返回null。
     */
    public Card askForCardFromPlayer(Player target, String prompt, boolean forced) {
        // 准备选项
        List<Card> choices = new ArrayList<>();
        if (target == this) {  // 选自己的牌
            choices.addAll(handCards); // TODO 弃装备牌
        } else {  // 选他人牌
            if (target.handCards.size() > 0) {  // 所有手牌作为一个选项，随机选一张
                Card hCards = new FakeCard( target.handCards.size() + "张手牌");
                hCards.addSubCards(target.handCards);
                choices.add(hCards);
            }
        }
        if (!choices.isEmpty()) {
            return chooseCard(choices, prompt, forced);
        }
        return null;  // 无牌可选
    }
    public Card askForCardFromPlayer(Player target) {
        return askForCardFromPlayer(target, "请选择一张牌：", true);
    }

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

    /**
     * 要求角色使用/打出一张闪
     * @param isUse true是使用，false是打出
     * @return 是否使用/打出
     */
    public boolean askForDodge(boolean isUse) {
        List<Card> dodges = handCards.stream().filter(card -> card instanceof Dodge).toList();
        Card card = chooseCard(dodges, isUse ? "请使用一张闪，0放弃：" : "请打出一张闪，0放弃：", false);
        if (card != null) {
            if (isUse) {
                useCard(card, new ArrayList<>());
            } else {
                responseCard(card, new ArrayList<>());
            }
        }
        return card != null;
    }

    public boolean askForSlash() {
        List<Card> dodges = handCards.stream().filter(card -> card instanceof Slash).toList();
        Card card = chooseCard(dodges, "请打出一张杀，0放弃：", false);
        if (card != null) responseCard(card, new ArrayList<>());
        return card != null;
    }
}
