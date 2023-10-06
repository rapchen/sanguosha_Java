package com.rapchen.sanguosha.core.pack;

import com.rapchen.sanguosha.core.general.standard.*;
import com.rapchen.sanguosha.core.general.wind.HuangZhong;

/**
 * 风包
 * @author Chen Runwen
 * @time 2023/10/5 13:32
 */
public class Wind extends Package {
    public Wind() {
        super();
        name = "wind";
        nameZh = "风包";
    }

    @Override
    public void init() {
        addGeneral(new HuangZhong());
//        addGeneral(new WeiYan());

//        addGeneral(new XiahouYuan());
//        addGeneral(new CaoRen());

//        addGeneral(new XiaoQiao());
//        addGeneral(new ZhouTai());

//        addGeneral(new ZhangJiao());
//        addGeneral(new YuJi());
    }
}
