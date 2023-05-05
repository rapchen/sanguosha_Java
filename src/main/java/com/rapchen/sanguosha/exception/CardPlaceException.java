package com.rapchen.sanguosha.exception;

/**
 * 卡牌出现在不应出现的位置
 * @author Chen Runwen
 * @time 2023/5/5 0:35
 */
public class CardPlaceException extends SgsRuntimeException {

    public CardPlaceException() {
    }

    public CardPlaceException(String message) {
        super(message);
    }
}
