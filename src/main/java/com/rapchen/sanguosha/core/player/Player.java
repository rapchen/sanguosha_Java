package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.*;
import com.rapchen.sanguosha.core.data.card.basic.*;
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

    // 卡牌位置相关
    public List<Card> handCards;  // 手牌
    public EquipArea equips;  // 装备区
    public List<DelayedTrickCard> judgeArea;  // 判定区的延时类锦囊列表，按照使用顺序排列
    public final Place HAND = new Place(Place.PlaceType.HAND, this);
    public final Place EQUIP = new Place(Place.PlaceType.EQUIP, this);
    public final Place JUDGE = new Place(Place.PlaceType.JUDGE, this);
    public final Place JUDGE_CARD = new Place(Place.PlaceType.JUDGE_CARD, this);

    public Phase phase = Phase.PHASE_OFF_TURN;  // 当前阶段
    public Fields xFields = new Fields();  // 额外字段，用于临时存储一些数据
    public Fields turnFields = new Fields();  // 回合字段，存储回合内的临时数据，回合结束销毁
    public Fields phaseFields = new Fields();  // 阶段字段，存储阶段内的临时数据，阶段结束销毁

    public Player(Engine engine, int id, String name) {
        this.engine = engine;
        this.id = id;
        this.name = name;
        this.handCards = new ArrayList<>();
        this.equips = new EquipArea(this);
        this.judgeArea = new ArrayList<>();
    }

    /**
     * 进行一个回合
     */
    public void doTurn() {
        log.warn("---------------------- {} 回合开始 ----------------------", this);
        engine.currentPlayer = this;  // 切换当前回合角色
        for (Player player : engine.players) {  // 重置所有角色的回合字段
            player.turnFields.clear();
        }
        // 回合开始时时机
        phase = Phase.PHASE_NONE;
        engine.trigger(new Event(Timing.TURN_BEGIN, this));

        // 执行各阶段
        tryPhase(Phase.PHASE_PREPARE);
        tryPhase(Phase.PHASE_JUDGE);
        tryPhase(Phase.PHASE_DRAW);
        tryPhase(Phase.PHASE_PLAY);
        tryPhase(Phase.PHASE_DISCARD);
        tryPhase(Phase.PHASE_END);

        // 回合结束时时机
        phase = Phase.PHASE_NONE;
        engine.trigger(new Event(Timing.TURN_END, this));
        phase = Phase.PHASE_OFF_TURN;
    }

    /* =============== begin 阶段 ================ */

    /**
     * 尝试执行一个阶段。涉及阶段跳过的判断和阶段执行逻辑
     * @param phase 阶段
     */
    private void tryPhase(Phase phase) {
        if (isPhaseSkipped(phase)) return;  // 判断是否已经被跳过
        for (Player player : engine.players) {  // 重置所有角色的阶段字段
            player.phaseFields.clear();
        }
        // 阶段开始前时机，触发阶段跳过和插入相关的技能
        engine.trigger(new Event(Timing.PHASE_BEFORE, this).withField("Phase", phase));
        if (isPhaseSkipped(phase)) return;

        this.phase = phase;
        // 阶段开始时机
        engine.trigger(new Event(Timing.PHASE_BEGIN, this).withField("Phase", phase));
        // 执行阶段原本的功能
        doPhase(phase);
    }

    /**
     * 执行一个阶段的具体功能
     * @param phase 阶段
     */
    private void doPhase(Phase phase) {
        switch (phase) {
            case PHASE_JUDGE -> doJudgePhase();
            case PHASE_DRAW -> doDrawPhase();
            case PHASE_PLAY -> doPlayPhase();
            case PHASE_DISCARD -> doDiscardPhase();
        }
    }

    /**
     * 判定阶段
     */
    private void doJudgePhase() {
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
        // 判断是否已跳过摸牌
        if (phaseFields.remove("DrawPhase_SkipDraw") != Boolean.TRUE) {
            int drawCount = 2;
            drawCount = engine.triggerModify(new Event(Timing.MD_DRAW_COUNT, this), drawCount);
            this.drawCards(drawCount);
        }
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
            int count = handCards.size() - hp;
            CardChoose choose = new CardChoose(this).fromSelf("h")
                    .count(count).forced().reason("DiscardPhase", String.format("弃牌阶段，请弃置%s张牌", count));
            askForDiscard(choose);
        }
    }

    public void skipPhase(Phase phase) {
        xFields.put("SkipPhase_" + phase.name, true);
    }

    public boolean isPhaseSkipped(Phase phase) {
        boolean skipped = xFields.remove("SkipPhase_" + phase.name) == Boolean.TRUE;
        if (skipped) log.warn("{} 跳过了 {}", this, phase);
        return skipped;
    }

    public boolean isCurrentPlayer() {
        return engine.currentPlayer == this;
    }

    /* =============== end 阶段 ================ */

    /* =============== begin 功能执行 ================ */
    // Card 相关
    public void drawCards(int count) {
        List<Card> cards = engine.getCardsFromDrawPile(count);
        obtain(cards, "drawCards");
        log.info("{} 摸了{}张牌：{}", this, cards.size(), Card.cardsToString(cards));
        // TODO 获得牌事件：只触发一次就行
    }

    /**
     * 弃牌
     */
    public void doDiscard(List<Card> cards, Player target) {
        log.warn("{} 弃置了{} {}张牌：{}", this, target == this ? "" : (" " + target),
                cards.size(), Card.cardsToString(cards));
        engine.moveToDiscard(cards);
    }
    public void doDiscard(List<Card> cards) {
        doDiscard(cards, this);
    }

    /**
     * 获得牌到手牌
     */
    public void obtain(Card card, String reason) {
        engine.moveCard(card, this.HAND, reason);
    }
    public void obtain(List<Card> cards, String reason) {
        engine.moveCards(cards, this.HAND, reason);
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
    protected void respondCard(Card card, List<Player> targets) {
        card.doRespond(this, targets);
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
                    hCards.place = target.HAND;  // 方便后续过滤
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
    public boolean inRange(Player target) {
        return getDistance(target) <= getRange();
    }

    /** 获取杀的次数限制 */
    public int getSlashLimit() {
        int limit = 1;
        limit = engine.triggerModify(new Event(Timing.MD_SLASH_LIMIT, this), limit);
        return limit;  // 默认1
    }

    /** 获取某类牌本阶段内已使用的次数 */
    public int getUsedTimes(Class<? extends Card> clazz, String pattern) {
        if ("phase".equals(pattern)) {
            return phaseFields.getInt("Used_" + clazz.getSimpleName(), 0);
        } else if ("turn".equals(pattern)) {
            return turnFields.getInt("Used_" + clazz.getSimpleName(), 0);
        }
        return 0;
    }
    public boolean hasUsed(Class<? extends Card> clazz, String pattern) {
        return getUsedTimes(clazz, pattern) >= 1;
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

    // 体力相关
    public void doDamage(Damage damage) {
        engine.doDamage(damage);
    }

    /**
     * 回复体力
     */
    public void doRecover(int count) {
        hp = Math.min(maxHp, hp + count);
        log.info("{} 回复了 {}点体力，目前体力为：{}/{}", this, count, hp, maxHp);
    }

    /**
     * 流失体力
     */
    public void loseHp(int count) {
        log.info("{} 流失了 {} 点体力", this, count);
        reduceHp(count);
    }

    /**
     * 扣减体力，并进行濒死判断。可以被伤害或体力流失触发。
     */
    public void reduceHp(int count) {
        hp -= count;
        engine.checkDeath(this);
    }

    public boolean injured() {
        return hp < maxHp;
    }

    /**
     * 进行一次判定，返回判定结果（判定牌、是否成功）
     */
    public Judgement doJudge(String nameZh, Function<Card, Boolean> judgeFunc) {
        return new Judgement(this, nameZh, judgeFunc).judge();
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
                engine.skills.add(skill, this);
            }
        }
        log.warn("{} 选择了武将 {}", this, general);
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
            if (targets == null) return false;  // TODO 应该也是把这个卡加到本次ask的黑名单，然后再选一次

            // 4. 使用
            useCard(card, targets);
            return true;
        }
    }

    protected abstract Card choosePlayCard(List<Card> cards);

    /**
     * 选择牌的目标
     * @param card 使用的牌
     */
    public List<Player> chooseTargets(Card card) {
        return card.chooseTargets(this);
    }

    /**
     * 要求弃牌
     * @param choose 选牌参数
     * @return 是否弃牌
     */
    public boolean askForDiscard(CardChoose choose) {
        if (choose.prompt == null) choose.prompt = String.format("请弃置 %s %d张牌：", choose.target, choose.count);
        List<Card> discards = choose.choose();

        if (discards == null) return false;  // 放弃弃牌了
        doDiscard(discards, choose.target);  // 执行弃牌：一起移动到弃牌堆
        return true;
    }

    /**
     * 要求角色选一张牌
     * @param choose 卡牌选择对象
     * @return 选择的牌。如果不选，就返回null。
     */
    public abstract Card chooseCard(CardChoose choose);

    /**
     * 要求角色选择一名角色
     * @param choose 角色选择对象
     * @return 选择的角色。如果不选，就返回null。
     */
    public abstract Player choosePlayer(PlayerChoose choose);

    /**
     * 要求角色选一个武将
     * @param generals 可选武将的列表
     * @param forced 是否必须选择
     * @param prompt 给用户的提示语
     * @param reason 选择原因，通常给AI做判断用
     * @return 选择的武将。如果不选，就返回null。
     */
    public abstract <T extends General> T chooseGeneral(List<T> generals, boolean forced, String prompt, String reason);

    /**
     * 要求用户选择是/否。
     */
    public boolean askForConfirm(String prompt, String reason) {
        List<String> choices = List.of("是");
        return chooseChoice(choices, false, prompt, reason) == 1;
    }

    /**
     * 要求用户进行选择。
     */
    public <T> T askForChoice(List<T> choices, boolean forced, String prompt, String reason) {
        List<String> strs = choices.stream().map(Object::toString).toList();
        int choice = chooseChoice(strs, forced, prompt, reason);
        return choice == 0 ? null : choices.get(choice-1);
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
        Card card = askForCard(ask);  // 要求角色提供闪
        if (card != null) {
            if (isUse) {
                useCard(card, new ArrayList<>());
            } else {
                respondCard(card, new ArrayList<>());
            }
        }
        return card != null;
    }

    /**
     * 要求角色打出一张杀
     * @return 是否打出
     */
    public boolean askForSlash(boolean isUse, Player target) {
        CardAsk ask = new CardAsk(Slash.class,
                isUse ? CardAsk.Scene.USE : CardAsk.Scene.RESPONSE,
                this, "askForSlash",
                isUse ? String.format("请对 %s 使用一张杀：", target) : "请打出一张杀：");
        Card card = askForCard(ask);
        if (card != null) {
            if (isUse) {
                useCard(card, List.of(target));
            } else {
                respondCard(card, new ArrayList<>());
            }
        }
        return card != null;
    }

    /**
     * 要求角色使用一张桃
     * @param target 求桃角色
     * @return 是否使用
     */
    private boolean askForPeach(Player target) {
        try (Fields.TmpField tf = xFields.tmpField("askForPeach_Target", target)) {
            CardAsk ask = new CardAsk(Peach.class, CardAsk.Scene.USE, this,
                    "askForPeach", String.format("%s 濒死，请求你使用一张桃，0放弃：", target));
            Card card = askForCard(ask);
            if (card != null) {
                useCard(card, Collections.singletonList(target));
            }
            return card != null;
        }
    }

    /**
     * 要求角色使用一张无懈可击
     * @param effect 无懈的目标卡牌
     * @return 是否使用
     */
    public boolean askForNullification(CardEffect effect) {
        List<Card> nullis = handCards.stream().filter(card -> card instanceof Nullification).toList();
        if (nullis.isEmpty()) return false;  // 没有无懈，用不了

        // 拼接用户提示语
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

        // 要求无懈卡牌
        Nullification nulli;
        try (Fields.TmpField tf = xFields.tmpField("askForNulli_CardEffect", effect)) {
            CardAsk ask = new CardAsk(Nullification.class, CardAsk.Scene.USE, this,
                    "askForNullification", prompt);
            nulli = (Nullification) askForCard(ask);
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
     * 要求角色使用/打出一张牌。这里处理可用牌、技能列表等逻辑，实际选牌交给chooseCard
     * @return 使用/打出的牌
     */
    public Card askForCard(CardAsk ask) {
        engine.trigger(new Event(Timing.CARD_ASKED, this).withField("CardAsk", ask));
        // 尝试获取技能提供的闪 TODO 一个技能提供卡后是否要打断该事件
        Card card = (Card) xFields.remove("CardProvided");
        if (card != null) return card;
        // 判断时机是否被打断
        while (true) {
            List<Card> cards = new ArrayList<>(handCards.stream().filter(ask::matches).toList());
            cards.addAll(engine.skills.getTransformedCards(ask));
            card = chooseCard(new CardChoose(this, cards, ask.forced, ask.reason, ask.prompt));
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
        return general + "(" + name + ")"
                + hp + '/' + maxHp +
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
