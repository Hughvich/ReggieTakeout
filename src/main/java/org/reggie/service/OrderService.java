package org.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggie.pojo.Orders;

public interface OrderService extends IService<Orders> {

    void submit(Orders orders);
}
