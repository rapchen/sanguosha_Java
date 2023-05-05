package com.rapchen.sanguosha.exception;

/**
 * 运行时异常父类，都是不应该出现的问题
 * @author Chen Runwen
 * @time 2023/5/5 0:35
 */
public class SgsRuntimeException extends RuntimeException {

    public SgsRuntimeException() {
    }

    public SgsRuntimeException(String message) {
        super(message);
    }
}
