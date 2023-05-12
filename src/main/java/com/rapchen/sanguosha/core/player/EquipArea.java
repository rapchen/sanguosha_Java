package com.rapchen.sanguosha.core.player;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.equip.EquipCard;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

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
     * 使用装备的逻辑。原来对应装备区的装备置入弃牌堆，新的置入对应装备区
     */
    public void useEquip(EquipCard equip) {
        EquipCard oldEquip = equips.get(equip.subType);
        if (oldEquip != null) {
            equips.remove(equip.subType);
            player.engine.moveToDiscard(oldEquip);
            log.warn("{} 装备区的 {} 被置入弃牌堆", player, oldEquip);
        }
        equips.put(equip.subType, equip);
    }

    /** 判断是否有某个子类的装备 */
    public boolean has(Card.SubType subType) {
        return equips.containsKey(subType);
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
