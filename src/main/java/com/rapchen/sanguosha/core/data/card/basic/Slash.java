package com.rapchen.sanguosha.core.data.card.basic;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.card.CardEffect;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;

/**
 * 杀
 * @author Chen Runwen
 * @time 2023/4/24 22:23
 */
public class Slash extends BasicCard {

    public Slash(Suit suit, Point point) {
        super(suit, point);
        name = "Slash";
        nameZh = "杀";
        subType = SubType.BASIC;
    }

    @Override
    public boolean validInPlayPhase(Player player) {
        return player.getUsedTimes(Slash.class, "phase") < player.getSlashLimit();
    }

    @Override
    public int distanceLimit(Player source, Player target) {
        return source.getRange();
    }

    @Override
    public void doEffect(CardEffect effect) {
        Player source = effect.getSource(), target = effect.target;
        boolean dodged = false;
        // 判断是否可闪避
        if (effect.xFields.remove("CannotDodge") != Boolean.TRUE) {
            dodged = askForDodge(effect, target);
        }
        // 触发杀被闪避事件
        if (dodged) {
            Engine.eg.trigger(new Event(Timing.SLASH_DODGED, source).withField("Target", target));
            if (source.xFields.remove("Slash_Undodged") == Boolean.TRUE) {  // 贯石斧等效果，闪避无效
                dodged = false;
            }
        }
        // 造成伤害
        if (!dodged) {
            source.doDamage(new Damage(effect));
        }
    }

    private boolean askForDodge(CardEffect effect, Player target) {
        // 需要的闪的数量。无双、肉林会修改
        int remainCount = 1;
        remainCount = Engine.eg.triggerModify(new Event(Timing.MD_CARD_ASK_COUNT, target)
                .withField("CardEffect", effect), remainCount);
        while (remainCount > 0) {
            if (!target.askForDodge(true)) return false;
            remainCount--;
        }
        return true;
    }
}
