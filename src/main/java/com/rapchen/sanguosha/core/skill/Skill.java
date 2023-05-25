package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.player.Player;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * 技能抽象类
 * @author Chen Runwen
 * @time 2023/5/14 12:03
 */
@Slf4j
public abstract class Skill {
    public String name;  // 技能名。
    public String nameZh;  // 中文技能名，用于显示。
    public Player owner;  // 技能拥有者
    public boolean compulsory = false;  // 是否是锁定技
    public boolean useByDefault = false;  // 是否默认发动

    public Skill(String name, String nameZh) {
        this.name = name;
        this.nameZh = nameZh;
    }

    /**
     * 打印技能释放日志
     */
    public void doLog() {
        log.warn("{} 发动了 {}", owner, nameZh);
    }
    public void doLog(String suffix) {
        log.warn("{} 发动了 {}, {}", owner, nameZh, suffix);
    }

    /**
     * 向某个角色询问是否使用本技能
     */
    public boolean askForUse(Player player) {
        if (useByDefault) return true;  // 默认发动
        int choice = player.askForChoice(null, false, String.format("是否发动 %s ?", nameZh), name);
        return choice == 1;
    }
    public boolean askForUse(Player player, Player target) {
        int choice = player.askForChoice(null, false,
                String.format("是否对 %s 发动 %s ?", target, nameZh), name);
        return choice == 1;
    }

    @Override
    public String toString() {
        return nameZh;
    }


    /**
     * 创造一个技能的实例
     * @param clazz 技能的类型
     */
    public static <T extends Skill> T createSkill(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            log.info("创建技能 {} 失败： {}", clazz.getName(), e.toString());
            return null;
        }
    }

}
