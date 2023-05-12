package com.rapchen.sanguosha.core.data.card;

import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.player.Player;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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
        POINT_NO("0"), POINT_A("A"), POINT_2("2"), POINT_3("3"), POINT_4("4"),
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
        EQUIP_HORSE_ATTACK(Type.EQUIPMENT, "进攻马"),
        EQUIP_HORSE_DEFEND(Type.EQUIPMENT, "防御马"),
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

    public static int nextCardId = 1;  // 自增ID，从1开始。保证实体卡牌的唯一性

    public Suit suit;
    public Point point;
    public SubType subType;
    protected int id;  // 在牌堆里的唯一ID，从1开始。虚拟卡<0
    protected String name;  // 牌名。对于基本牌和锦囊牌，牌名即对象的类名；对于装备牌，同类的可能不同名（如赤兔和大宛）
    protected String nameZh;  // 中文牌名，用于显示。

    public boolean virtual = false;  // 是否虚拟卡
    public List<Card> subCards;  // 子卡，通常用于虚拟卡
    public boolean throwAfterUse = true;  // 使用完毕后是否进入弃牌堆。默认进入
    public int benefit = -100;  // 对于目标来说的有益程度。越大越有益，0为无关，负数有害
    public Fields xFields;  // 额外字段，用于临时存储一些数据

    /**
     * 创建真实卡牌，ID自增
     */
    public Card(Suit suit, Point point) {
        this(suit, point, nextCardId);
        nextCardId++;
    }

    /**
     * ID自定，通常是虚拟卡牌
     */
    public Card(Suit suit, Point point, int id) {
        this.suit = suit;
        this.point = point;
        this.id = id;
        this.xFields = new Fields();
    }

    public void addSubCards(List<Card> cards) {
        if (subCards == null) subCards = new ArrayList<>();
        subCards.addAll(cards);
    }
    public void addSubCard(Card card) {
        if (subCards == null) subCards = new ArrayList<>();
        subCards.add(card);
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
            if (canUseTo(source, target)) targets.add(target);
        }
        return targets;
    }

    /**
     * 检测此牌是否可用。默认能对所有其他角色使用。
     * @param source 使用者
     * @param target 目标
     */
    public boolean canUseTo(Player source, Player target) {
        return target != source;
    }

    /**
     * 使用牌，执行牌的效果。模板方法
     *
     * @param source  使用者
     * @param targets 目标
     */
    public void doUse(Player source, List<Player> targets) {
        doUseLog(source, targets);
        // 弃牌 TODO 不在手牌？虚拟牌？
        source.handCards.remove(this);
        if (throwAfterUse) {
            source.engine.moveToDiscard(this);
        }
        // 执行效果
        CardUse use = new CardUse(this, source, targets);
        doUseToAll(use);
        for (Player target : targets) {
            CardEffect effect = new CardEffect(use, target);
            use.currentTarget = target;
            // 对每个目标生效前，询问无懈
            if (!checkCanceled(effect)) {
                doEffect(source, target);  // TODO 改use
            }
        }
        doAfterUse(source, targets);
    }

    private void doUseLog(Player source, List<Player> targets) {
        log.info("{} {} 使用了 {}", source,
                targets.isEmpty() ? "" : ("对 " + Player.playersToString(targets)),
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
    public void doEffect(Player source, Player target) {}

    /** 牌使用完的后处理（如五谷牌进弃牌堆） */
    public void doAfterUse(Player source, List<Player> targets) {}

    /**
     * 打出牌
     * @param source  使用者
     * @param targets 目标。例如冲阵可能会用
     */
    public void doResponse(Player source, List<Player> targets) {
        log.info("{} 打出了 {}", source, this);
        // 弃牌 TODO 不在手牌？虚拟牌？
        source.handCards.remove(this);
        source.engine.moveToDiscard(this);
    }

    @Override
    public String toString() {
        return nameZh + "[" + suit + point + "]" + id;
    }

    public static String cardsToString(List<? extends Card> cards) {
        return cardsToString(cards, false);
    }

    public static String cardsToString(List<? extends Card> cards, boolean withNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (withNumber) {
                sb.append(i + 1).append(": ");
            }
            sb.append(card);
            if (i < cards.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
