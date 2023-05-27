package org.reggie.common;
/*
自定义异常业务类
 */
public class CustomException extends RuntimeException {

    // 定义异常，传入提示信息
    public CustomException(String message) {
        super(message);
    }
}
