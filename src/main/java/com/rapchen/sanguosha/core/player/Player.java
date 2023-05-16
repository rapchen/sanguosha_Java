package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.*;
import com.rapchen.sanguosha.core.data.card.basic.*;
import com.rapchen.sanguosha.core.data.card.equip.EquipCard;
import com.rapchen.sanguosha.core.data.card.equip.Weapon;
import com.rapchen.sanguosha.core.data.card.trick.DelayedTrickCard;
import com.rapchen.sanguosha.core.data.card.trick.Nullification;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Skill;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.exception.BadPlayerException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
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
    public EquipArea equips;  // 装备区
    public List<DelayedTrickCard> judgeArea;  // 判定区的延时类锦囊列表，按照使用顺序排列
    public Phase phase = Phase.PHASE_OFF_TURN;  // 当前阶段
    public int slashTimes = 1;
    public Fields xFields;  // 额外字段，用于临时存储一些数据

    public Player(Engine engine, int id, String name) {
        this.engine = engine;
        this.id = id;
        this.name = name;
        this.handCards = new ArrayList<>();
        this.equips = new EquipArea(this);
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
            if (!trick.askForNullification(effect)) {
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
            askForDiscard(handCards.size() - hp, "h");
        }
    }

    /**
     * 结束阶段
     */
    private void doEndPhase() {
        phase = Phase.PHASE_END;
        engine.invoke(new Event(Timing.PHASE_BEGIN, this)
                .withField("Phase", Phase.PHASE_END));
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
    // Card 相关
    public void drawCards(int count) {
        List<Card> cards = engine.getCardsFromDrawPile(count);
        handCards.addAll(cards);
        log.info("{} 摸了{}张牌：{}", this.name, cards.size(), Card.cardsToString(cards));
        // TODO 获得牌事件：只触发一次就行
    }

    /**
     * 从角色处移除一张牌
     * @return 是否成功移除。找不到牌就返回false
     */
    public boolean doRemoveCard(Card card) {
        if (card.isVirtual()) {  // 对于虚拟牌，移除所有的子卡。移除至少一张视为成功
            int removedCnt = doRemoveCards(card.subCards);
            return removedCnt > 0;
        }
        if (handCards.remove(card)) return true;
        if (equips.remove(card)) {
            ((EquipCard)card).onRemove();
            return true;
        }
        if (judgeArea.remove(card)) return true;
        return false;
    }

    /**
     * 从角色处移除多张牌
     * @return 成功移除牌的数量
     */
    public int doRemoveCards(List<Card> cards) {
        int count = 0;
        for (Card card : cards) {
            if (doRemoveCard(card)) count++;
        }
        return count;
    }

    /**
     * 弃牌。目前只有将牌加入弃牌堆的逻辑。从原位移除的部分在doRemoveCard
     */
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

    /**
     * 获取角色处牌的数量
     * @param pattern 区域。含h为手牌，e为装备区，j为判定区。如hej为计算所有区域
     */
    public int getCardCount(String pattern) {
        int count = 0;
        if (pattern.contains("h")) count += handCards.size();
        if (pattern.contains("e")) count += equips.size();
        if (pattern.contains("j")) count += judgeArea.size();
        return count;
    }

    // Player 相关
    /** 获取其他玩家，按当前回合角色顺序 */
    public List<Player> getOtherPlayers() {
        List<Player> players = new ArrayList<>(engine.getAllPlayers());
        players.remove(this);
        return players;
    }

    /** 获取攻击范围 */
    public int getRange() {
        Weapon weapon = equips.getWeapon();
        if (weapon != null) return weapon.range;
        return 1;  // 默认1
    }

    /** 计算距离 */
    public int getDistance(Player target) {
        if (target == this) return 0;  // 到自己的距离永远是0
        int index = this.getOtherPlayers().indexOf(target);
        if (index == -1) {  // 找不到target
            throw new BadPlayerException("没有找到目标：" + target);
        }
        // 取左右距离中较小值
        int distance = Math.min(index + 1, this.getOtherPlayers().size() - index);
        // 距离修正 TODO 放到技能里面
        if (this.equips.has(Card.SubType.EQUIP_HORSE_OFF)) distance -= 1;
        if (target.equips.has(Card.SubType.EQUIP_HORSE_DEF)) distance += 1;
        return Math.max(distance, 1);  // 距离至少为1
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

    /** 添加技能 */
    public void addSkill(Class<? extends Skill> skillClass) {
        try {
            Skill skill = skillClass.getConstructor().newInstance();
            skill.owner = this;
            engine.skills.add(skill);
        } catch (NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            log.error("获取技能 {} 失败！ {}", skillClass, e.toString());
            e.printStackTrace();
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
    public boolean askForDiscard(int count, String pattern) {
        return askForDiscard(count, this, true, pattern, null);
    }
    /**
     * 要求弃牌
     *
     * @param count  弃牌数量
     * @param target 弃牌目标
     * @param forced 是否必须选择
     * @param patten 区域。hej
     * @param prompt 提示语。默认为"请弃置{count}张牌："
     * @return 是否弃牌
     */
    public boolean askForDiscard(int count, Player target, boolean forced, String patten, String prompt) {
        List<Card> discards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card card = askForCardFromPlayer(target, forced, patten,
                    prompt == null ? String.format("请弃置%d张牌：", count) : prompt, "askForDiscard");
            if (card == null) continue;
            discards.add(card);
            target.doRemoveCard(card);  // 从角色处移除卡牌，避免重复选择（这里不触发失去牌的时机）
        }
        if (discards.size() < count) {
            return false;  // 弃牌张数不够，放弃弃牌了
        }
        doDiscard(discards);  // 弃牌、TODO 失去牌时机应该是在这里一起发生，不是一张张
        log.info("{} 弃了 {} {}张牌：{}", this.name, target.name, discards.size(), Card.cardsToString(discards));
        return true;
    }

    protected Card chooseDiscard(Player target) {
        return chooseDiscard(target, target.handCards); // TODO
    }
    protected abstract Card chooseDiscard(Player target, List<Card> cards);

    /**
     * 要求角色从目标角色的牌中选一张牌
     * @param target  目标角色
     * @param forced  是否必须选择
     * @param pattern 区域。含h为手牌，e为装备区，j为判定区。
     * @param prompt  给用户的提示语
     * @param reason  选牌原因，通常给AI做判断用
     * @return 选择的牌。如果选择了虚拟牌，会自动随机其中的子卡。如果不选或无牌可选，就返回null。
     */
    public Card askForCardFromPlayer(Player target, boolean forced, String pattern, String prompt, String reason) {
        // 准备选项
        List<Card> choices = new ArrayList<>();
        if (pattern.contains("h")) {  // 手牌
            if (target == this) {  // 选自己的牌
                choices.addAll(handCards);
            } else {  // 选他人牌
                if (target.handCards.size() > 0) {  // 所有手牌作为一个选项，随机选一张
                    Card hCards = new FakeCard( target.handCards.size() + "张手牌");
                    hCards.addSubCards(target.handCards);
                    choices.add(hCards);
                }
            }
        }
        if (pattern.contains("e")) {  // 装备
            choices.addAll(target.equips.getAll());
        }
        if (pattern.contains("j")) {  // 判定区
            choices.addAll(target.judgeArea);
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
        log.warn(getDetail(true));
        for (Player player : engine.players) {
            if (player == this) continue;
            log.warn(player.getDetail(false));
        }
    }

    @Override
    public String toString() {
        return name;  // TODO 将名+位置？
    }

    /**
     * 获取角色详细信息，用于打印
     * @param self 是否是本人视角（其他人视角看不到手牌）
     */
    public String getDetail(boolean self) {
        return name + "(P" + id +
                ") " + hp + '/' + maxHp +
                ", 判定: " + Card.cardsToString(judgeArea) +
                ", 装备: " + equips +
                ", 手牌: " + (self ? Card.cardsToString(handCards) : handCards.size());
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
