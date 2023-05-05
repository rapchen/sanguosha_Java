package com.rapchen.sanguosha.core.pack;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.card.Card;

/**
 * 扩展包的抽象父类
 * @author Chen Runwen
 * @time 2023/5/5 17:29
 */
public abstract class Package {

    protected Engine engine;

    protected String name;  // 包名
    protected String nameZh;  // 中文包名，用于显示。
    boolean isCardPack = false;  // 卡牌包还是武将包

    public Package(Engine engine) {
        this.engine = engine;
    }

    /**
     * 游戏开始时，进行初始化。对于卡牌包是加入牌堆；武将包是加入将池
     */
    public void init() {
    }

    public void addCard(Card card) {
        engine.table.drawPile.add(card);

    }
}
