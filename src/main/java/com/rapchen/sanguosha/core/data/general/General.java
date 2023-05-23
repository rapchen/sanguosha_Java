package com.rapchen.sanguosha.core.data.general;

import com.rapchen.sanguosha.core.skill.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * 武将对象
 * @author Chen Runwen
 * @time 2023/4/24 18:01
 */
public abstract class General {

    public static enum Gender {
        GENDER_NO("无性别"), MALE("男"), FEMALE("女");

        public final String nameZh;
        Gender(String nameZh) {
            this.nameZh = nameZh;
        }
    }

    public String name;
    public String nameZh;
    public Gender gender = Gender.MALE;
    public int maxHp;
    public List<Class<? extends Skill>> skills = new ArrayList<>();

    public General(String name, String nameZh, Gender gender, int maxHp) {
        this.name = name;
        this.nameZh = nameZh;
        this.gender = gender;
        this.maxHp = maxHp;
    }

    @Override
    public String toString() {
        return nameZh;
    }
}
