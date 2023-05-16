package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.player.Player;
import lombok.extern.slf4j.Slf4j;

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

    @Override
    public String toString() {
        return nameZh;
    }
}
