package com.rapchen.sanguosha.exception;

/**
 * 异常父类，是可以出现的问题，如游戏结束
 * @author Chen Runwen
 * @time 2023/5/5 11:43
 */
public class SgsException extends Exception{

    public SgsException() {
    }

    public SgsException(String message) {
        super(message);
    }
}
