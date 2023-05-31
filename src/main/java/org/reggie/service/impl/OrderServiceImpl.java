package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.common.BaseContext;
import org.reggie.common.CustomException;
import org.reggie.mapper.OrderMapper;
import org.reggie.pojo.*;
import org.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        // 获得当前用户id
        Long userId = BaseContext.getCurrentId();
        // 查询用户购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper); // (已在前端判断过shoppingCartList购物车为空)
        // 查询用户数据，和地址
        User user = userService.getById(userId);
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) throw new CustomException("地址为空，不能下单");
        long orderId = IdWorker.getId();
        //设置金额，AtomicInteger原子整数类，多线程安全
        AtomicInteger amount = new AtomicInteger(0);
        // 遍历购物车，整合OrderDetail，拿到金额
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId); //关联订单 orderId
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount()); // 单份金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue()); //addAndGet 单份金额 * 份数
            return orderDetail;
        }).collect(Collectors.toList());

        // 设置订单号，...
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        // 下单，订单表order里插入一条订单数据orders，包括上面的设置订单号，...
        this.save(orders);
        // 订单明细order_detail插入多条数据，
        orderDetailService.saveBatch(orderDetailList);
        // 清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
