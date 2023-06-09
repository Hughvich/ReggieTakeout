package org.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    HttpServletRequest request;

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充insert");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", request.getSession().getAttribute("employee"));
        metaObject.setValue("updateUser", request.getSession().getAttribute("employee"));
        // 也可通过BaseContext获取id
//        metaObject.setValue("updateUser", BaseContext.getCurrentId());


    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充update");

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", request.getSession().getAttribute("employee"));
    }
}
