package org.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.mapper.OrderDetailMapper;
import org.reggie.pojo.OrderDetail;
import org.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
