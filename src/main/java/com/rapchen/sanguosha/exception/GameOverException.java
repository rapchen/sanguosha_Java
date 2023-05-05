package com.rapchen.sanguosha.exception;

/**
 * 游戏结束。 TODO 不是Runtime
 * @author Chen Runwen
 * @time 2023/5/5 11:43
 */
public class GameOverException extends SgsRuntimeException {

    public GameOverException() {
    }

    public GameOverException(String message) {
        super(message);
    }
}
