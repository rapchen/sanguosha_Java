package com.rapchen.sanguosha.core.general.standard;

import com.rapchen.sanguosha.core.data.Damage;
import com.rapchen.sanguosha.core.data.Judgement;
import com.rapchen.sanguosha.core.data.card.Card;
import com.rapchen.sanguosha.core.data.card.CardChoose;
import com.rapchen.sanguosha.core.general.General;
import com.rapchen.sanguosha.core.player.Player;
import com.rapchen.sanguosha.core.skill.Event;
import com.rapchen.sanguosha.core.skill.Timing;
import com.rapchen.sanguosha.core.skill.TriggerSkill;

/**
 * 司马懿
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class SimaYi extends General {
    public SimaYi() {
        super("SimaYi", "司马懿", Gender.MALE, Nation.WEI, 3);
        skills.add(FanKui.class);
        skills.add(GuiCai.class);
    }

    // 反馈: 每当你受到伤害后，你可以获得伤害来源的一张牌。
    public static class FanKui extends TriggerSkill {
        public FanKui() {
            super("FanKui", "反馈", new Timing[]{Timing.DAMAGED_DONE});
            useByDefault = true;
        }

        @Override
        public void onTrigger(Event event) {
            final Damage damage = (Damage) event.xFields.get("Damage");
            if (damage.effect == null || damage.effect.getSource() == null) return;
            Player source = damage.effect.getSource();

            if (source.alive && askForUse(owner)) {
                CardChoose choose = new CardChoose(owner).fromPlayer(source, "he")
                        .reason(name, "请选择要获取的牌：");
                Card card = choose.chooseOne();
                if (card == null) return;
                owner.obtain(card, name);
                doLog(String.format("获得了 %s 的 %s", source, card));
            }
        }
    }

    // 鬼才: 每当一名角色的判定牌生效前，你可以打出一张手牌代替之。
    public static class GuiCai extends TriggerSkill {
        public GuiCai() {
            super("GuiCai", "鬼才", new Timing[]{Timing.JUDGE_BEFORE_EFFECT});
            onlyOwner = false;
        }

        @Override
        public void onTrigger(Event event) {
            final Judgement judge = (Judgement) event.xFields.get("Judge");
            String prompt = String.format("%s 的 %s 判定牌 %s 即将生效，是否选择一张手牌代替？",
                    judge.player, judge.nameZh, judge.card);
            CardChoose choose = new CardChoose(owner).fromSelf("h")
                    .reason(name, prompt);

            Card card = choose.chooseOne();
            if (card == null) return;
            judge.rejudge(card, this, false);
            doLog(String.format("将 %s 的 %s 判定牌替换为 %s", judge.player, judge.nameZh, card));
        }
    }
}
