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
        addGeneral(new LiuBei());
        addGeneral(new GuanYu());
        addGeneral(new ZhangFei());
        addGeneral(new ZhaoYun());
        addGeneral(new MaChao());
//        addGeneral(new ZhugeLiang());
        addGeneral(new HuangYueying());

        addGeneral(new CaoCao());
        addGeneral(new SimaYi());
        addGeneral(new GuoJia());
        addGeneral(new ZhenJi());
        addGeneral(new XiahouDun());
        addGeneral(new XuChu());
        addGeneral(new ZhangLiao());

        addGeneral(new SunQuan());
        addGeneral(new SunShangxiang());
        addGeneral(new ZhouYu());
        addGeneral(new LvMeng());
        addGeneral(new LuXun());
        addGeneral(new DaQiao());
        addGeneral(new GanNing());
        addGeneral(new HuangGai());

        addGeneral(new DiaoChan());
        addGeneral(new LvBu());
        addGeneral(new HuaTuo());
        addGeneral(new BaiBan());
    }
}
