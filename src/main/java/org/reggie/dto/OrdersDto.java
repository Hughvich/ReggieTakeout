package org.reggie.dto;


import lombok.Data;
import org.reggie.pojo.OrderDetail;
import org.reggie.pojo.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
