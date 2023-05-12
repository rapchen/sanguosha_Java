package com.rapchen.sanguosha.exception;

/**
 * 角色有问题
 * @author Chen Runwen
 * @time 2023/5/5 0:35
 */
public class BadPlayerException extends SgsRuntimeException {

    public BadPlayerException() {
    }

    public BadPlayerException(String message) {
        super(message);
    }
}
