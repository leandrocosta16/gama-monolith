package com.thesis.gama.model;

import com.thesis.gama.dto.OrderItemSetDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int qty;
    private double priceAtBuyTime; //sou eu que incializo com product.getPrice
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /**
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    **/

    private String productName;
    private int productId;
    private float weight;

    public OrderItem(OrderItemSetDTO orderItemSetDTO, Product p) {
        this.qty = orderItemSetDTO.getQty();
        this.productName = p.getName();
        this.productId = p.getId();
        this.weight = p.getWeight();
        if(p.getPromotionPrice() == null) {
            this.priceAtBuyTime = p.getPrice();
        } else {
            this.priceAtBuyTime = p.getPromotionPrice();
        }

    }

    public OrderItem(ShoppingCartItem shoppingCartItem) {
        this.qty = shoppingCartItem.getQty();
        //this.product = shoppingCartItem.getProduct();
        this.productName = shoppingCartItem.getProduct().getName();
        this.productId = shoppingCartItem.getProduct().getId();
        this.weight = shoppingCartItem.getProduct().getWeight();
        if(shoppingCartItem.getProduct().getPromotionPrice() == null) {
            this.priceAtBuyTime = shoppingCartItem.getProduct().getPrice();
        } else {
            this.priceAtBuyTime = shoppingCartItem.getProduct().getPromotionPrice();
        }

    }
}
