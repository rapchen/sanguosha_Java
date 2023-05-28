package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardUse;
import com.rapchen.sanguosha.core.data.card.basic.Slash;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 马超
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class MaChao extends General {
    public MaChao() {
        super("MaChao", "马超", Gender.MALE, Nation.SHU, 4);
        skills.add(MaShu.class);
        skills.add(TieQi.class);
    }

    // 马术: 锁定技，你与其他角色的距离-1。
    public static class MaShu extends TriggerSkill {
        public MaShu() {
            super("MaShu", "马术", new Timing[]{Timing.MD_DISTANCE});
            compulsory = true;
        }

        @Override
        public int onModify(Event event, int value) {
            return value - 1;
        }
    }

    // 铁骑: 每当你指定【杀】的目标后，你可以进行判定：若结果为红色，该角色不能使用【闪】响应此【杀】。
    public static class TieQi extends TriggerSkill {
        public TieQi() {
            super("TieQi", "铁骑", new Timing[]{Timing.TARGET_CHOSEN});
        }

        @Override
        public void onTrigger(Event event) {
            final CardUse use = (CardUse) event.xFields.get("CardUse");
            if (!(use.card instanceof Slash)) return;
            for (Player target : use.targets) {
                if (askForUse(owner, target)) {
                    Judgement judge = owner.doJudge(nameZh, Card::isRed);
                    if (judge.success) {
                        doLog(String.format("此杀不可被 %s 闪避", target));
                        // 设置不可被target闪避的字段
                        use.card.xFields.put("CannotDodge", target.idStr(), true);
                    }
                }
            }
        }
    }
}
