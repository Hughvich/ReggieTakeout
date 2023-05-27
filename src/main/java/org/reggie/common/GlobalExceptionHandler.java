package org.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/*
    全局异常捕获机制：底层基于代理
 */


// @RestControllerAdvice：拦截哪些类型的controller？
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常处理器，捕获字段重复的异常处理
     * @param exception
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String > exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());

        // 判断异常信息类型，针对账号重复
        if (exception.getMessage().contains("Duplicate entry")) {
            String[] split = exception.getMessage().split(" ");
            String msg = split[2] + " 已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 捕获CustomException异常，对删除菜品/套餐分类时关联了菜品/套餐的异常处理
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String > exceptionHandler(CustomException exception) {
        log.error(exception.getMessage());

        return R.error(exception.getMessage());
    }


}
