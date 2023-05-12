package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.*;
import com.rapchen.sanguosha.core.data.card.basic.*;
import com.rapchen.sanguosha.core.data.card.trick.DelayedTrickCard;
import com.rapchen.sanguosha.core.data.card.trick.Nullification;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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
    public List<Card> handCards;  // 手牌
//    public Map<Card.SubType, EquipCard> equips;  // 装备，每种类型一个
    public List<DelayedTrickCard> judgeArea;  // 判定区的延时类锦囊列表，按照使用顺序排列
    public Phase phase = Phase.PHASE_OFF_TURN;  // 当前阶段
    public int slashTimes = 1;
    public Fields xFields;  // 额外字段，用于临时存储一些数据

    public Player(Engine engine, int id, String name) {
        this.engine = engine;
        this.id = id;
        this.name = name;
        this.handCards = new ArrayList<>();
        this.judgeArea = new ArrayList<>();
        // TODO 武将
        this.hp = 4;
        this.maxHp = 4;
        this.xFields = new Fields();
    }

    /**
     * 进行一个回合
     */
    public void doTurn() {
        log.warn("---------------------- 回合开始 ----------------------");
        engine.currentPlayer = this;  // 切换当前回合角色
        // TODO 回合开始时机
        doPreparePhase();
        doJudgePhase();
        doDrawPhase();
        if (!isPhaseSkipped(Phase.PHASE_PLAY)) {
            doPlayPhase();
        }
        doDiscardPhase();
        doEndPhase();
        // TODO 回合结束时机
        phase = Phase.PHASE_OFF_TURN;
    }

    /* =============== begin 阶段 ================ */

    /**
     * 准备阶段。 TODO 各个阶段的开始结束共同逻辑可以用AOP，考虑整合SpringAOP
     */
    private void doPreparePhase() {
        phase = Phase.PHASE_PREPARE;
    }

    /**
     * 判定阶段
     */
    private void doJudgePhase() {
        phase = Phase.PHASE_JUDGE;
        // 闪电判定之后有可能回到原角色判定区，为了避免死循环要拷贝一份
        ArrayList<DelayedTrickCard> tricks = new ArrayList<>(judgeArea);
        // 后发先至，从后使用的开始依次判定
        Collections.reverse(tricks);
        for (DelayedTrickCard trick : tricks) {
            // 先询问无懈
            CardUse use = new CardUse(trick, this, null);  // 这里source其实没意义，但不能不传
            CardEffect effect = new CardEffect(use, this);
            if (!trick.checkCanceled(effect)) {
                trick.doDelayedEffect(this);  // 执行延时锦囊的延时效果
            }
            judgeArea.remove(trick);
            trick.doAfterDelayedEffect(this);  // 延时效果之后的处理，默认进弃牌堆
        }
    }

    /**
     * 摸牌阶段
     */
    private void doDrawPhase() {
        phase = Phase.PHASE_DRAW;
        this.drawCards(2);
    }

    /**
     * 出牌阶段
     */
    private void doPlayPhase() {
        phase = Phase.PHASE_PLAY;
        slashTimes = 1;
        while (true) {
            if (!askForPlayCard()) break;
        }
    }

    /**
     * 弃牌阶段
     */
    private void doDiscardPhase() {
        phase = Phase.PHASE_DISCARD;
        if (handCards.size() > hp) {
            askForDiscard(handCards.size() - hp);
        }
    }

    /**
     * 结束阶段
     */
    private void doEndPhase() {
        phase = Phase.PHASE_END;
    }

    public void skipPhase(Phase phase) {
        xFields.put("SkipPhase_" + phase.name, null);
    }

    public boolean isPhaseSkipped(Phase phase) {
        boolean skipped = xFields.containsKey("SkipPhase_" + phase.name);
        xFields.remove("SkipPhase_" + phase.name);
        if (skipped) log.warn("{} 跳过了 {}", this, phase);
        return skipped;
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
        engine.moveToDiscard(card);
    }
    protected void doDiscard(List<Card> cards) {
        engine.moveToDiscard(cards);
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

    /** 获取其他玩家，按当前回合角色顺序 */
    public List<Player> getOtherPlayers() {
        List<Player> players = new ArrayList<>(engine.getAllPlayers());
        players.remove(this);
        return players;
    }

    public void doDamage(Player target, int damageCount) {
        engine.doDamage(this, target, damageCount);
    }

    /**
     * 回复体力
     */
    public void doRecover(int count) {
        hp = Math.min(maxHp, hp + count);
        log.info("{} 回复了 {}点体力，目前体力为：{}/{}", this.name, count, hp, maxHp);
    }

    /**
     * 进行一次判定，返回判定结果（判定牌、是否成功）
     */
    public Judgement doJudge(String nameZh, Function<Card, Boolean> judgeFunc) {
        Card card = engine.getCardFromDrawPile();
        // 改判要插在这里
        engine.moveToDiscard(card);
        Boolean success = judgeFunc.apply(card);
        String successStr = (success == null) ? "完成" : (success ? "成功" : "失败");
        log.warn("{} 的 {} 判定 {}，结果为 {}", this, nameZh, successStr, card);
        return new Judgement(card, success);
    }

    /**  濒死，请求救援 */
    public void callRescue() {
        // 求桃，对每个角色依次求桃，每个角色可以给多次
        for (Player player : engine.getAllPlayers()) {
            while (hp <= 0) {
                if (!player.askForPeach(this)) break;
            }
        }
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

    protected abstract Card choosePlayCard(List<Card> cards);

    /**
     * 选择牌的目标 TODO 现在自动选目标，后面要改成手动选
     * @param card 使用的牌
     */
    private List<Player> chooseTargets(Card card) {
        return card.getAvailableTargets(this);
    }

    /**
     * 弃自己的牌
     */
    public boolean askForDiscard(int count) {
        return askForDiscard(count, this, true);
    }
    /**
     * 要求弃牌
     * @param count  弃牌数量
     * @param target 弃牌目标
     * @param forced 是否必须选择
     */
    public boolean askForDiscard(int count, Player target, boolean forced) {
        List<Card> discards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card card = askForCardFromPlayer(target, forced, "请弃置一张牌：", "askForDiscard");
            if (card == null) continue;
            discards.add(card);
            target.handCards.remove(card);  // TODO 这里弃牌时机应该是一起发生
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
     *
     * @param target 目标角色
     * @param forced 是否必须选择
     * @param prompt 给用户的提示语
     * @param reason 选牌原因，通常给AI做判断用
     * @return 选择的牌。如果选择了虚拟牌，会自动随机其中的子卡。如果不选或无牌可选，就返回null。
     */
    public Card askForCardFromPlayer(Player target, boolean forced, String prompt, String reason) {
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
            Card card;
            try (Fields.TmpField tf = xFields.tmpField("askForCardFromPlayer_Reason", reason);
                    Fields.TmpField tf2 = xFields.tmpField("askForCardFromPlayer_Target", target)) {
                card = chooseCard(choices, forced, prompt, "askForCardFromPlayer");
            }
            if (card == null) return null;
            if (card.isVirtual()) {  // 对于虚拟牌（如所有手牌）自动随机其中一张子卡
                int num = engine.random.nextInt(card.subCards.size());
                card = card.subCards.get(num);
            }
            return card;
        }
        return null;  // 无牌可选
    }
//    public Card askForCardFromPlayer(Player target, String reason) {
//        return askForCardFromPlayer(target, "请选择一张牌：", true, reason);
//    }

    /**
     * 要求用户选一张牌
     * @param cards  可选牌的列表
     * @param forced 是否必须选择
     * @param prompt 给用户的提示语
     * @param reason 选牌原因，通常给AI做判断用
     * @return 选择的牌。如果不选，就返回null。
     */
    public abstract Card chooseCard(List<Card> cards, boolean forced, String prompt, String reason);


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
        Card card = chooseCard(dodges, false,
                isUse ? "请使用一张闪，0放弃：" : "请打出一张闪，0放弃：", "askForDodge");
        if (card != null) {
            if (isUse) {
                useCard(card, new ArrayList<>());
            } else {
                responseCard(card, new ArrayList<>());
            }
        }
        return card != null;
    }

    /**
     * 要求角色打出一张杀
     * @return 是否打出
     */
    public boolean askForSlash() {
        List<Card> slashes = handCards.stream().filter(card -> card instanceof Slash).toList();
        Card card = chooseCard(slashes, false, "请打出一张杀，0放弃：", "askForSlash");
        if (card != null) responseCard(card, new ArrayList<>());
        return card != null;
    }

    /**
     * 要求角色使用一张桃
     * @param target 求桃角色
     * @return 是否使用
     */
    private boolean askForPeach(Player target) {
        List<Card> peaches = handCards.stream().filter(card -> card instanceof Peach).toList();
        String prompt = String.format("%s 濒死，请求你使用一张桃，0放弃：", target);
        Card card = null;  // 使用的无懈卡牌
        try (Fields.TmpField tf = xFields.tmpField("askForPeach_Target", target)) {
            card = chooseCard(peaches, false, prompt, "askForPeach");
        }
        if (card != null) {
            useCard(card, Collections.singletonList(target));
        }
        return card != null;
    }

    /**
     * 要求角色使用一张无懈可击
     * @param effect 无懈的目标卡牌
     * @return 是否使用
     */
    public boolean askForNullification(CardEffect effect) {
        List<Card> nullis = handCards.stream().filter(card -> card instanceof Nullification).toList();
        if (nullis.isEmpty()) return false;  // 没有无懈，用不了
        // 用户提示语
        Card card = effect.getCard();
        String prompt;
        if (card instanceof DelayedTrickCard) {
            prompt = String.format("%s 判定区的 %s 即将生效, 是否使用无懈可击？0放弃：",
                    effect.target, card);
        } else {
            String target = card instanceof Nullification ?  // 无懈的目标是牌的使用，其他牌的目标是角色
                    ((Nullification) card).targetEffect.toString():
                    effect.target.toString();
            prompt = String.format("%s 使用了 %s, 目标是 %s, 是否使用无懈可击？0放弃：",
                    effect.getSource(), card, target);
        }

        Nullification nulli = null;  // 使用的无懈卡牌
        try (Fields.TmpField tf = xFields.tmpField("askForNulli_CardEffect", effect)) {
            nulli = (Nullification) chooseCard(nullis, false, prompt, "askForNullification");
        }
        if (nulli != null) {
            nulli.targetEffect = effect;  // 标记这张无懈的目标牌
            useCard(nulli, new ArrayList<>());
            nulli.targetEffect = null;
            if (nulli.xFields.containsKey("Nullified")) {
                nulli.xFields.remove("Nullified");
                nulli = null;
            }
        }
        return nulli != null;
    }

    /* =============== end 要求玩家操作的方法 ================ */

    /* =============== begin 工具方法 ================ */

    /** 玩家视角的打印桌面 */
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
        return name;  // TODO 将名+位置？
    }

    public String getDetail() {
        return "Player " + id + '(' + name +
                ") HP " + hp + '/' + maxHp +
                ", 判定区: " + Card.cardsToString(judgeArea) +
                ", 手牌: " + Card.cardsToString(handCards);
    }

    public String getDetailForOthers() {
        return "Player " + id + '(' + name +
                ") HP " + hp + '/' + maxHp +
                ", 判定区: " + Card.cardsToString(judgeArea) +
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

    /* =============== end 工具方法 ================ */
}
