package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.*;
import com.rapchen.sanguosha.core.data.card.basic.*;
import com.rapchen.sanguosha.core.data.card.equip.EquipCard;
import com.rapchen.sanguosha.core.data.card.equip.Weapon;
import com.rapchen.sanguosha.core.data.card.trick.DelayedTrickCard;
import com.rapchen.sanguosha.core.data.card.trick.Nullification;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.TransformSkill;
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
    public General.Gender gender = General.Gender.GENDER_NO;
    public General general;  // 武将
    public List<Skill> skills = new ArrayList<>();  // 技能

    public List<Card> handCards;  // 手牌
    public EquipArea equips;  // 装备区
    public List<DelayedTrickCard> judgeArea;  // 判定区的延时类锦囊列表，按照使用顺序排列

    public Phase phase = Phase.PHASE_OFF_TURN;  // 当前阶段
    public int slashTimes = 0;  // 当前出牌阶段已使用的杀的数量
    public Fields xFields;  // 额外字段，用于临时存储一些数据

    public Player(Engine engine, int id, String name) {
        this.engine = engine;
        this.id = id;
        this.name = name;
        this.handCards = new ArrayList<>();
        this.equips = new EquipArea(this);
        this.judgeArea = new ArrayList<>();
        this.xFields = new Fields();
    }

    /**
     * 进行一个回合
     */
    public void doTurn() {
        log.warn("---------------------- {} 回合开始 ----------------------", this);
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
            CardEffect effect = new CardEffect(trick, null, this);
            if (!trick.askForNullification(effect)) {
                trick.doDelayedEffect(effect);  // 执行延时锦囊的延时效果
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
        slashTimes = 0;  // 重置杀使用数
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
            askForDiscard(handCards.size() - hp, "h", "DiscardPhase");
        }
    }

    /**
     * 结束阶段
     */
    private void doEndPhase() {
        phase = Phase.PHASE_END;
        engine.trigger(new Event(Timing.PHASE_BEGIN, this)
                .withField("Phase", Phase.PHASE_END));
    }

    public void skipPhase(Phase phase) {
        xFields.put("SkipPhase_" + phase.name, true);
    }

    public boolean isPhaseSkipped(Phase phase) {
        boolean skipped = xFields.remove("SkipPhase_" + phase.name) == Boolean.TRUE;
        if (skipped) log.warn("{} 跳过了 {}", this, phase);
        return skipped;
    }

    /* =============== end 阶段 ================ */

    /* =============== begin 功能执行 ================ */
    // Card 相关
    public void drawCards(int count) {
        List<Card> cards = engine.getCardsFromDrawPile(count);
        engine.moveCards(cards, Card.Place.HAND, this, "drawCards");
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
    public void useCard(Card card, List<Player> targets) {
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

    /**
     * 获取角色处牌的列表
     * @param target 目标角色。当前角色与目标角色不同时，手牌不可见（作为一张虚拟牌出现）
     * @param pattern 区域。含h为手牌，e为装备区，j为判定区。如hej为计算所有区域
     */
    public List<Card> getCards(Player target, String pattern) {
        List<Card> cards = new ArrayList<>();
        if (pattern.contains("h")) {  // 手牌
            if (target == this) {  // 自己手牌
                cards.addAll(handCards);
            } else {  // 他人手牌，不可见
                if (target.handCards.size() > 0) {  // 所有手牌作为一个选项，随机选一张
                    Card hCards = new FakeCard( target.handCards.size() + "张手牌");
                    hCards.addSubCards(target.handCards);
                    cards.add(hCards);
                }
            }
        }
        if (pattern.contains("e")) {  // 装备
            cards.addAll(target.equips.getAll());
        }
        if (pattern.contains("j")) {  // 判定区
            cards.addAll(target.judgeArea);
        }
        return cards;
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

    /** 获取杀的次数限制 */
    public int getSlashLimit() {
        int limit = 1;
        limit = engine.triggerModify(new Event(Timing.MD_SLASH_LIMIT, this), limit);
        return limit;  // 默认1
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
        distance = engine.triggerModify(
                new Event(Timing.MD_DISTANCE, this).withField("Target", target), distance);
        // 距离至少为1，这个逻辑优先于所有距离修正
        return Math.max(distance, 1);
    }

    public void doDamage(Damage damage) {
        engine.doDamage(damage);
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
        card.place = Card.Place.JUDGE_CARD;
        // 改判和获取判定牌要插在这里
        engine.moveToDiscard(card, Card.Place.JUDGE_CARD);  // 仍在处理区的进入弃牌堆
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

    // General、Skill相关
    /**
     * 设置武将
     */
    public void setGeneral(General general) {
        this.general = general;
        gender = general.gender;
        maxHp = general.maxHp;
        hp = maxHp;
        for (Class<? extends Skill> skillClass : general.skills) {
            Skill skill = Skill.createSkill(skillClass);
            if (skill != null) {
                skill.owner = this;
                engine.skills.add(skill);
            }
        }
        log.warn("{} 选择了武将 {}", this, general);
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
        CardAsk ask = new CardAsk(CardAsk.Scene.PLAY, this);
        while (true) {
            // 1. 先找出可以使用的牌
            List<Card> cards = new ArrayList<>(handCards.stream()
                    .filter(card -> card.canUseInPlayPhase(this)).toList());
            // 可用的转化技
            cards.addAll(engine.skills.getTransformedCards(ask));

            // 2. 选牌
            Card card = choosePlayCard(cards);
            if (card == null) return false;  // 放弃选择，直接返回
            // 选择转化技之后的处理逻辑
            if (card.isVirtual() && card.skill instanceof TransformSkill skill) {
                card = skill.askForTransform(ask);
                if (card == null) continue;  // 转化失败，剔除该技能，重新选择
            }

            // 3. 选择目标
            List<Player> targets = chooseTargets(card);

            // 4. 使用
            useCard(card, targets);
            return true;
        }
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
    public boolean askForDiscard(int count, String pattern, String reason) {
        return askForDiscard(count, this, true, pattern, null, reason);
    }
    /**
     * 要求弃牌
     *
     * @param count  弃牌数量
     * @param target 弃牌目标
     * @param forced 是否必须选择
     * @param patten 区域。hej
     * @param prompt 提示语。默认为"请弃置{count}张牌："
     * @param reason 选牌原因，通常给AI做判断用
     * @return 是否弃牌
     */
    public boolean askForDiscard(int count, Player target, boolean forced, String patten, String prompt, String reason) {
        List<Card> discards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card card = askForCardFromPlayer(target, forced, patten,
                    prompt == null ? String.format("请弃置 %s %d张牌：", target, count) : prompt, reason);
            if (card == null) continue;
            discards.add(card);
            target.doRemoveCard(card);  // 从角色处移除卡牌，避免重复选择（这里不触发失去牌的时机）
        }
        if (discards.size() < count) {
            return false;  // 弃牌张数不够，放弃弃牌了
        }
        doDiscard(discards);  // 执行弃牌：一起移动到弃牌堆
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
        List<Card> choices = getCards(target, pattern);

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
    public abstract <T extends Card> T chooseCard(List<T> cards, boolean forced, String prompt, String reason);

    /**
     * 要求用户选一个武将
     * @param generals 可选武将的列表
     * @param forced 是否必须选择
     * @param prompt 给用户的提示语
     * @param reason 选择原因，通常给AI做判断用
     * @return 选择的武将。如果不选，就返回null。
     */
    public abstract <T extends General> T chooseGeneral(List<T> generals, boolean forced, String prompt, String reason);

    /**
     * 要求用户进行选择（默认为选是否）。
     * @param forced 是否必须选择，非必选的话可以用0跳过
     */
    public int askForChoice(List<String> choices, boolean forced, String prompt, String reason) {
        if (choices == null) {
            choices = List.of("是");
        }
        return chooseChoice(choices, forced, prompt, reason);
    }

    protected abstract int chooseChoice(List<String> choices, boolean forced, String prompt, String reason);

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
        CardAsk ask = new CardAsk(Dodge.class,
                isUse ? CardAsk.Scene.USE : CardAsk.Scene.RESPONSE,
                this, "askForDodge",
                isUse ? "请使用一张闪：" : "请打出一张闪：");
        engine.trigger(new Event(Timing.CARD_ASKED, this).withField("CardAsk", ask));
        Card card = (Card) xFields.remove("CardProvided");  // 尝试获取技能提供的闪 TODO 一个技能提供卡后是否要打断该事件
        if (card == null) {
            card = askForCard(ask);  // 要求角色提供闪
        }
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
        CardAsk ask = new CardAsk(Slash.class, CardAsk.Scene.RESPONSE, this,
                "askForSlash", "请打出一张杀，0放弃：");
        Card card = askForCard(ask);
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
            if (nulli.xFields.remove("Nullified") == Boolean.TRUE) {
                nulli = null;  // 如果这张无懈被无懈了，就视为没有
            }
        }
        return nulli != null;
    }

    /**
     * 要求角色使用/打出一张牌。这里处理可用牌、技能列表等逻辑，实际选牌交给chooseCard TODO
     * @return 使用/打出的牌
     */
    public Card askForCard(CardAsk ask) {
        while (true) {
            List<Card> cards = new ArrayList<>(handCards.stream().filter(ask::matches).toList());
            cards.addAll(engine.skills.getTransformedCards(ask));
            Card card = chooseCard(cards, ask.forced, ask.prompt, ask.reason);
            if (card == null) return null;
            // 选择转化技之后的处理逻辑
            if (card.isVirtual() && card.skill instanceof TransformSkill skill) {
                card = skill.askForTransform(ask);
                if (card == null) continue;  // 转化失败，剔除该技能，重新选择
            }
            return card;
        }
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
        return general + "(" + name + ")";
    }

    public String idStr() {
        return String.valueOf(id);
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
