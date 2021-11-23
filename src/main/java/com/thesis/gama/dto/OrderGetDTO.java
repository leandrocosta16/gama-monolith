package com.thesis.gama.dto;

import com.thesis.gama.model.Order;
import com.thesis.gama.model.OrderItem;
import com.thesis.gama.model.OrderStatus;
import com.thesis.gama.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderGetDTO {
    private int id;
    private Date buyDate;
    private OrderStatus orderStatus;
    private String email;
    private List<OrderItemGetDTO> orderItems;
    private double totalPrice;
    private double shippingCost;

    public OrderGetDTO(Order order){
        this.id = order.getId();
        this.buyDate = order.getBuyDate();
        this.orderStatus = order.getOrderStatus();
        this.email = order.getUser().getAccount().getEmail();

        if(order.getOrderItems() == null){
            this.orderItems = new ArrayList<>();
        }
        else {
            this.orderItems = order.getOrderItems().stream().map(OrderItemGetDTO::new).collect(Collectors.toList());
        }

        this.totalPrice = order.getTotalPrice();
        this.shippingCost = order.getShipping().getCost();
    }
}
