package com.rapchen.sanguosha.core.data.general.standard;

import com.rapchen.sanguosha.core.data.general.General;
import com.rapchen.sanguosha.core.skill.BiYue;
import com.rapchen.sanguosha.core.skill.Skill;

import java.util.List;

/**
 * 貂蝉 TODO 离间
 * @author Chen Runwen
 * @time 2023/5/24 0:32
 */
public class DiaoChan extends General {
    public DiaoChan() {
        super("DiaoChan", "貂蝉", Gender.FEMALE, 3);
        skills.add(BiYue.class);
    }
}
