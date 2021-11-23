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
@Table(name="shoppingcart_item")
public class ShoppingCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int qty;
    @ManyToOne
    @JoinColumn(name = "shoppingcart_id")
    private ShoppingCart shoppingcart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;



    public ShoppingCartItem(OrderItemSetDTO orderItemSetDTO, Product p) {
        this.qty = orderItemSetDTO.getQty();
        this.product = p;

    }
}
