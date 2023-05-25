package com.rapchen.sanguosha.core.pack;

import com.rapchen.sanguosha.core.general.standard.*;

/**
 * 标准包（武将包）
 * @author Chen Runwen
 * @time 2023/5/5 17:32
 */
public class Standard extends Package {
    public Standard() {
        super();
        name = "standard";
        nameZh = "标准包";
    }

    @Override
    public void init() {
//        addGeneral(new LiuBei());
        addGeneral(new GuanYu());
        addGeneral(new ZhangFei());
        addGeneral(new ZhaoYun());
        addGeneral(new MaChao());
        addGeneral(new HuangYueying());

        addGeneral(new CaoCao());

        addGeneral(new DiaoChan());
        addGeneral(new BaiBan());
    }
}
