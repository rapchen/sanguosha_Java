package com.rapchen.sanguosha.core.data.card.equip;

import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;
import lombok.extern.slf4j.Slf4j;

/**
 * 青釭剑
 * @author Chen Runwen
 * @time 2023/5/14 0:08
 */
@Slf4j
public class QinggangSword extends Weapon {
    public QinggangSword(Suit suit, Point point) {
        super(suit, point, "QinggangSword", "青釭剑", 2);
        skill = new QinggangSwordSkill();
    }

    // 锁定技，当你使用【杀】指定一名角色为目标后，无视其防具。
    // 【杀】成功指定目标后，效果开始。（如果目标是大乔，流离在先，被流离的目标是【杀】成功指定的目标）
    // 目标对【杀】的响应结束----结束。（最终的结果有两种，被【闪】抵消，或不出【闪】受到伤害）
    // TODO 失去青釭剑不会导致无视防具失效。因此效果结束的地方要判断所有人，同时失去装备技能不能直接移除，而是要加一个valid字段
    private static class QinggangSwordSkill extends TriggerSkill {
        public QinggangSwordSkill() {
            super("QinggangSwordSkill", "青釭剑", new Timing[]{Timing.TARGET_CHOSEN, Timing.CARD_USED});
            compulsory = true;
        }

        @Override
        public void onTrigger(Event event) {
            Timing timing = event.timing;
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            if (!(use.card instanceof Slash)) return;

            if (timing == Timing.TARGET_CHOSEN) {  // 指定目标时，上一个无视防具标记
                for (Player target : use.targets) {
                    target.xFields.put("ArmorInvalid", name, true);
                    if (target.equips.has(SubType.EQUIP_ARMOR)) {
                        log.warn("{} 的 {} 生效，{} 的防具无效", owner, nameZh, target);
                    }
                }
            } else if (timing == Timing.CARD_USED) {  // 卡牌使用完毕，移除标记
                for (Player target : use.targets) {
                    target.xFields.remove("ArmorInvalid", name);
                }
            }
        }
    }
}
