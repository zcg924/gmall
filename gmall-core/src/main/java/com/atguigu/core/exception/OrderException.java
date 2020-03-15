package com.atguigu.core.exception;

/**
 * @author zcgstart
 * @create 2020-03-12 23:27
 */
public class OrderException extends RuntimeException{

    public OrderException() {
        super();
    }

    public OrderException(String message) {
        super(message);
    }
}
