package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderHisDto {

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;

    public OrderHisDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }
}
