package com.rapchen.sanguosha.core.general;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.pack.Standard;
import com.rapchen.sanguosha.core.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 武将管理器
 * @author Chen Runwen
 * @time 2023/5/24 22:45
 */
public class GeneralManager {
    public List<General> generals = new ArrayList<>();

    public void init() {
        new Standard().init();
        Collections.shuffle(generals);
    }

    public void chooseGeneral(Player player) {
//        List<General> subList = generals.subList(0, 3);
        List<General> subList = generals;  // 做武将测试，不卡数量
        General general = player.chooseGeneral(subList, true, "请选择你的武将：", "chooseGeneral");
        player.setGeneral(general);
    }
}
