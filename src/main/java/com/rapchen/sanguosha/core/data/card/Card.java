package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Skill;
import com.rapchen.sanguosha.core.skill.Timing;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Chen Runwen
 * @time 2023/4/14 12:17
 */
@Slf4j
@Data
public abstract class Card {

    enum Color {
        COLOR_NO, RED, BLACK;
    }

    public enum Suit {
        SUIT_NO(Color.COLOR_NO, "无色"),
        HEART(Color.RED, "♥"),
        DIAMOND(Color.RED, "♦"),
        SPADE(Color.BLACK, "♠"),
        CLUB(Color.BLACK, "♣"),
        SUIT_NO_RED(Color.RED, "红"),
        SUIT_NO_BLACK(Color.BLACK, "黑");

        public final Color color;
        public final String token;

        Suit(Color color, String token) {
            this.color = color;
            this.token = token;
        }

        public static final Suit[] REGULAR_SUITS = new Suit[]{HEART, DIAMOND, SPADE, CLUB};

        @Override
        public String toString() {
            return token;
        }
    }

    public enum Point {
        POINT_NO(""), POINT_A("A"), POINT_2("2"), POINT_3("3"), POINT_4("4"),
        POINT_5("5"), POINT_6("6"), POINT_7("7"), POINT_8("8"), POINT_9("9"),
        POINT_10("10"), POINT_J("J"), POINT_Q("Q"), POINT_K("K");
        public final String token;
        public static final Point[] REGULAR_POINTS = new Point[]{POINT_A, POINT_2, POINT_3, POINT_4, POINT_5,
                POINT_6, POINT_7, POINT_8, POINT_9, POINT_10, POINT_J, POINT_Q, POINT_K};

        Point(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return token;
        }

        public boolean gt(Point o) {
            return compareTo(o) > 0;
        }
        public boolean ge(Point o) {
            return compareTo(o) >= 0;
        }
    }

    enum Type {
        TYPE_NO("无类别"), BASIC("基本牌"), TRICK("锦囊牌"), EQUIPMENT("装备牌");

        public final String name;

        Type(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
        public static final Type[] REGULAR_TYPES = new Type[]{BASIC, TRICK, EQUIPMENT};
    }

    public enum SubType {
        SUBTYPE_NO(Type.TYPE_NO, "无子类别"),
        BASIC(Type.BASIC, "基本牌"),
        TRICK_DELAYED(Type.TRICK, "延时锦囊"),
        TRICK_IMMEDIATE(Type.TRICK, "非延时锦囊"),
        EQUIP_WEAPON(Type.EQUIPMENT, "武器"),
        EQUIP_ARMOR(Type.EQUIPMENT, "防具"),
        EQUIP_HORSE_DEF(Type.EQUIPMENT, "防御马"),
        EQUIP_HORSE_OFF(Type.EQUIPMENT, "进攻马"),
        EQUIP_TREASURE(Type.EQUIPMENT, "宝物");

        public final Type type;
        public final String name;

        SubType(Type type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Place {
        DRAW,  // 摸牌堆
        DISCARD,  // 弃牌堆
        HAND,  // 手牌
        EQUIP,  // 装备区
        JUDGE,  // 判定区
        JUDGE_CARD,  // 判定牌（临时，算是处理区的一种）
        HANDLE,  // 处理区（临时）
        EXTRA,  // 游戏外，角色的额外牌堆
    }

    public static int nextCardId = 1;  // 自增ID，从1开始。保证实体卡牌的唯一性

    public Suit suit;
    public Point point;
    public SubType subType;
    public int id = -1;  // 在牌堆里的唯一ID，从1开始。虚拟卡<0
    public String name;  // 牌名。对于基本牌和锦囊牌，牌名即对象的类名；对于装备牌，同类的可能不同名（如赤兔和大宛）
    public String nameZh;  // 中文牌名，用于显示。
    public Place place;  // 卡牌位置。
    public Player owner = null;  // 卡牌当前所属区域的角色（判定区和判定牌都算）。不属于某个角色则为null

    public boolean virtual = false;  // 是否虚拟卡
    public List<Card> subCards = new ArrayList<>();  // 子卡，通常用于虚拟卡
    public Skill skill = null;  // 生成卡牌的技能。通常是转化技
    public boolean throwAfterUse = true;  // 使用完毕后是否进入弃牌堆。默认进入
    public int benefit = -100;  // 对于目标来说的有益程度。越大越有益，0为无关，负数有害
    public Fields xFields = new Fields();  // 额外字段，用于临时存储一些数据

    /**
     * 创建卡牌。ID自增由Package做
     */
    public Card(Suit suit, Point point) {
        this.suit = suit;
        this.point = point;
    }

    /**
     * ID自定，通常是虚拟卡牌
     */
    public Card(Suit suit, Point point, int id) {
        this.suit = suit;
        this.point = point;
        this.id = id;
    }

    public void addSubCards(List<Card> cards) {
        if (subCards == null) subCards = new ArrayList<>();
        subCards.addAll(cards);
    }
    public void addSubCard(Card card) {
        if (subCards == null) subCards = new ArrayList<>();
        subCards.add(card);
    }

    public Color getColor() {
        return suit.color;
    }
    public boolean isRed() {
        return suit.color == Color.RED;
    }
    public boolean isBlack() {
        return suit.color == Color.BLACK;
    }

    /* =============== end 通用功能执行 ================ */

    /* =============== begin 子类需要实现的具体功能 ================ */

    /**
     * 检查出牌阶段是否可使用。模板方法，先检查是否合法，然后检查是否有可用目标
     * @param player 使用者
     */
    public final boolean canUseInPlayPhase(Player player) {
        return validInPlayPhase(player) && !getAvailableTargets(player).isEmpty();
    }

    /** 出牌阶段是否合法。这里不检查目标。 */
    public boolean validInPlayPhase(Player player) {
        return true;
    }

    /**
     * 此牌可用的所有目标。使用canUseTo依次检测
     * @param source 使用者
     */
    public List<Player> getAvailableTargets(Player source) {
        List<Player> targets = new ArrayList<>();
        for (Player target : source.engine.getAllPlayers()) {
            if (canUseToDistance(source, target)) targets.add(target);
        }
        return targets;
    }

    /**
     * 检测此牌是否可用。模板方法，在canUseTo的基础上检查距离。
     * @param source 使用者
     * @param target 目标
     */
    public boolean canUseToDistance(Player source, Player target) {
        int distanceLimit = distanceLimit(source, target);
        Event event = new Event(Timing.MD_DISTANCE_LIMIT, source)
                .withField("Target", target)
                .withField("Card", this);
        distanceLimit = Engine.eg.triggerModify(event, distanceLimit);  // 触发距离限制的修正
        return canUseTo(source, target) &&
                source.getDistance(target) <= distanceLimit;
    }

    /**
     * 检测此牌是否可用（忽略距离检查）。默认能对所有其他角色使用。
     * @param source 使用者
     * @param target 目标
     */
    public boolean canUseTo(Player source, Player target) {
        return target != source;
    }

    /**
     * 此牌可用的最大距离限制。默认无限制。
     * @param source 使用者
     * @param target 目标
     */
    public int distanceLimit(Player source, Player target) {
        return 10000;
    }

    /**
     * 使用牌，执行牌的效果。模板方法
     *
     * @param source  使用者
     * @param targets 目标
     */
    public void doUse(Player source, List<Player> targets) {
        doUseLog(source, targets);
        // 移动到处理区
        Engine.eg.moveCard(this, Place.HANDLE, null, "Use");

        // 执行效果
        CardUse use = new CardUse(this, source, targets);
        Engine.eg.trigger(new Event(Timing.CARD_USING, source).withField("CardUse", use));  // 卡牌使用时
        // TODO 这里插入改变目标的时机
        Engine.eg.trigger(new Event(Timing.TARGET_CHOSEN, source).withField("CardUse", use));  // 指定目标后

        doUseToAll(use);  // 全体效果
        for (Player target : targets) {  // 单个目标的效果
            CardEffect effect = new CardEffect(use, target);
            use.currentTarget = target;
            // 对每个目标生效前，询问无懈
            if (!checkCanceled(effect)) {
                doEffect(effect);
            }
        }
        doAfterUse(use);  // 后处理

        // 结算完毕，处理区的牌进入弃牌堆（已经被奸雄等技能获得的不动）
        if (throwAfterUse) {
            source.engine.moveToDiscard(this, Place.HANDLE);
        }
        Engine.eg.trigger(new Event(Timing.CARD_USED, source).withField("CardUse", use));  // 结算完毕
    }

    private void doUseLog(Player source, List<Player> targets) {
        log.info("{}{} 使用了 {}", source,
                targets.isEmpty() ? "" : (" 对 " + Player.playersToString(targets)),
                this);
    }

    /**
     * 对所有人使用的效果。如果对每个人效果都一样，那可以直接重写doEffect。
     */
    public void doUseToAll(CardUse use) {}

    /**
     * 判断是否需要取消对单个目标使用的效果。如询问无懈可击
     */
    public boolean checkCanceled(CardEffect effect) {
        return false;  // 默认不取消
    }

    /**
     * 对单个目标使用的效果。如果对每个人效果都一样，那可以直接重写这个，否则用doUseToAll。
     */
    public void doEffect(CardEffect effect) {}

    /** 牌使用完的后处理（如五谷牌进弃牌堆） */
    public void doAfterUse(CardUse use) {}

    /**
     * 打出牌
     * @param source  使用者
     * @param targets 目标。例如冲阵可能会用
     */
    public void doRespond(Player source, List<Player> targets) {
        log.info("{} 打出了 {}", source, this);
        // 移到弃牌堆
        Engine.eg.moveCard(this, Place.DISCARD, null, "Response");
        // 打出结束时机
        Engine.eg.trigger(new Event(Timing.CARD_RESPONDED, source).withField("Card", this));
    }

    /* =============== end 子类需要实现的具体功能 ================ */

    /* =============== begin 工具方法 ================ */

    @Override
    public String toString() {
        return nameZh + "[" + suit + point + "]" + (id > 0 ? id : "");
    }

    public static String cardsToString(Collection<? extends Card> cards) {
        return cardsToString(cards, false);
    }

    public static String cardsToString(Collection<? extends Card> cards, boolean withNumber) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Iterator<? extends Card> iter = cards.iterator(); iter.hasNext(); ) {
            Card card = iter.next();
            if (withNumber) {  // 带上序号
                sb.append(i).append(": ");
            }
            sb.append(card);
            if (iter.hasNext()) sb.append(", ");  // 如果不是最后一个，带上逗号
            i++;
        }
        return sb.toString();
    }

    /**
     * 创造一个无花色点数的临时虚拟牌
     * @param clazz 牌的类型
     */
    public static <T extends Card> T createTmpCard(Class<T> clazz) {
        try {
            T card = clazz.getConstructor(Suit.class, Point.class).newInstance(Suit.SUIT_NO, Point.POINT_NO);
            card.virtual = true;
            return card;
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            log.info("创建虚拟牌 {} 失败： {}", clazz.getName(), e.toString());
            return null;
        }
    }

    /**
     * 根据子卡列表创造一个对应花色点数的虚拟牌
     * @param clazz 牌的类型
     * @param subCards 子卡
     */
    public static <T extends Card> T createVirtualCard(Class<T> clazz, List<Card> subCards) {
        T tmpCard = createTmpCard(clazz);
        if (tmpCard == null) return null;
        tmpCard.addSubCards(subCards);
        tmpCard.refreshSuitPoint();
        return tmpCard;
    }

    /**
     * 根据子卡重新确定虚拟卡的花色和点数
     */
    public void refreshSuitPoint() {
        if (subCards == null || subCards.size() == 0) {
            suit = Suit.SUIT_NO;
            point = Point.POINT_NO;
        } else if (subCards.size() == 1) {
            suit = subCards.get(0).suit;
            point = subCards.get(0).point;
        } else {
            point = Point.POINT_NO;
            boolean isRed = true, isBlack = true;
            for (Card subCard : subCards) {
                if (!subCard.isRed()) isRed = false;
                if (!subCard.isBlack()) isBlack = false;
            }
            suit = isRed ? Suit.SUIT_NO_RED : (isBlack ? Suit.SUIT_NO_BLACK : Suit.SUIT_NO);
        }
    }

    /**
     * 判断一名角色是否可以在出牌阶段使用某种牌
     * @param clazz 牌的类型
     * @param source 角色
     */
    public static boolean validInPlayPhase(Class<? extends Card> clazz, Player source) {
        Card card = createTmpCard(clazz);
        if (card == null) return false;
        return card.validInPlayPhase(source);
    }

    /* =============== end 工具方法 ================ */
}
