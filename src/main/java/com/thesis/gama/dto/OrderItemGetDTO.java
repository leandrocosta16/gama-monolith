package com.thesis.gama.dto;

import com.thesis.gama.model.Order;
import com.thesis.gama.model.OrderItem;
import com.thesis.gama.model.OrderStatus;
import com.thesis.gama.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItemGetDTO {
    private int qty;
    private double priceAtBuyTime;
    private int productId;
    private String productName;

    public OrderItemGetDTO(OrderItem orderItem){
        this.qty = orderItem.getQty();
        this.priceAtBuyTime = orderItem.getPriceAtBuyTime();
        this.productId = orderItem.getProductId();
        this.productName = orderItem.getProductName();
    }

}
