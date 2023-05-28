package com.rapchen.sanguosha.core.skill;

import com.rapchen.sanguosha.core.common.Fields;
import com.rapchen.sanguosha.core.player.Player;

/**
 * 事件枚举类
 * @author Chen Runwen
 * @time 2023/5/14 12:20
 */
public class Event {
    public Timing timing;
    public Player player;
    public Fields xFields;

    public Event(Timing timing, Player player) {
        this.timing = timing;
        this.player = player;
        this.xFields = new Fields();
    }

    /** 添加字段，支持链式编程 */
    public Event withField(String key, Object value) {
        this.xFields.put(key, value);
        return this;
    }
}
