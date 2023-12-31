package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.equip.EquipCard;
import com.rapchen.sanguosha.core.data.card.equip.Weapon;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 装备区
 * @author Chen Runwen
 * @time 2023/5/12 13:01
 */
@Slf4j
public class EquipArea {
    private final Player player;
    public Map<Card.SubType, EquipCard> equips;  // 装备，每种子类型一个

    public static final Card.SubType[] subTypes = new Card.SubType[]{Card.SubType.EQUIP_WEAPON,
            Card.SubType.EQUIP_ARMOR, Card.SubType.EQUIP_HORSE_DEF,
            Card.SubType.EQUIP_HORSE_OFF, Card.SubType.EQUIP_TREASURE};

    public EquipArea(Player player) {
        this.player = player;
        this.equips = new HashMap<>();
    }

    /**
     * 放置装备的底层逻辑。原来对应装备区的装备置入弃牌堆，新的置入对应装备区
     */
    public void putEquip(EquipCard equip) {
        EquipCard oldEquip = equips.get(equip.subType);
        if (oldEquip != null) {
            Engine.eg.moveToDiscard(oldEquip);
            log.warn("{} 装备区的 {} 被置入弃牌堆", player, oldEquip);
        }
        equips.put(equip.subType, equip);
    }

    /**
     * 移除装备的底层逻辑
     * @return 是否有这张装备
     */
    public boolean remove(Card card) {
        if (card instanceof EquipCard equip) {
            if (equips.get(equip.subType) == equip) {
                equips.remove(equip.subType);
                equip.onRemove();
                return true;
            }
        }
        return false;
    }

    /** 判断是否有某个子类的装备 */
    public boolean has(Card.SubType subType) {
        return equips.containsKey(subType);
    }

    /** 获取某个子类的装备。没有返回null */
    public EquipCard get(Card.SubType subType) {
        return equips.get(subType);
    }
    public Weapon getWeapon() {
        return (Weapon) equips.get(Card.SubType.EQUIP_WEAPON);
    }

    /** 返回所有装备的列表 */
    public List<EquipCard> getAll() {
        return new ArrayList<>(equips.values());
    }

    public int size() {
        return equips.size();
    }

    /** 防具是否有效 */
    public boolean isArmorValid() {
        Fields subFields = (Fields) player.xFields.get("ArmorInvalid");
        return subFields == null || subFields.isEmpty();
    }

    @Override
    public String toString() {
        String[] strs = new String[5];
        for (int i = 0; i < subTypes.length; i++) {
            Card.SubType subType = subTypes[i];
            strs[i] = equips.get(subType) == null ? " " : equips.get(subType).toString();
        }
        return String.join("|", strs);
    }
}
