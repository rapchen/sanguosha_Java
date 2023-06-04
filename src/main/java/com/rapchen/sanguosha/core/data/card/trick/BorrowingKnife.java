package com.rapchen.sanguosha.core.data.card.trick;

import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.data.card.equip.Weapon;
import com.rapchen.sanguosha.core.player.Player;
import lombok.extern.slf4j.Slf4j;

/**
 * 借刀杀人
 * @author Chen Runwen
 * @time 2023/5/5 18:46
 */
@Slf4j
public class BorrowingKnife extends ImmediateTrickCard {
    public BorrowingKnife(Suit suit, Point point) {
        super(suit, point);
        name = "BorrowingKnife";
        nameZh = "借刀杀人";
        maxTargetCount = 2;  // 需要选择两个角色，虽然锦囊的目标只有第一个
    }

    @Override
    public boolean canUseTo(Player source, Player target) {
        if (chosenTargets.isEmpty()) {  // 选择借刀杀人的目标
            return super.canUseTo(source, target) && target.equips.has(SubType.EQUIP_WEAPON);
        }
        Player slashSource = chosenTargets.get(0);
        Card card = createTmpCard(Slash.class);
        return card.canUseTo(slashSource, target);  // 选择杀的目标
    }

    @Override
    public boolean targetsValid() {
        if (chosenTargets.size() < 2) return false;
        // 在这里把杀的目标从锦囊实际目标中移除，放到额外字段里
        Player target = chosenTargets.get(1);
        xFields.put("Slash_Target", target);
        chosenTargets.remove(target);
        return true;
    }

    @Override
    public void doEffect(CardEffect effect) {
        Player source = effect.getSource(), slashSource = effect.target;
        Player target = (Player) effect.getCard().xFields.get("Slash_Target");
        if (target == null) return;
        if (!slashSource.askForSlash(true, target)) {
            Weapon weapon = slashSource.equips.getWeapon();
            if (weapon != null) {
                log.info("{} 从 {} 处获得了 {}", source, target, weapon);
                source.obtain(weapon, name);
            }
        }
    }

}
