package com.thesis.gama.dto;

import com.thesis.gama.model.OrderItem;
import com.thesis.gama.model.ShoppingCartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.PortUnreachableException;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShoppingCartItemGetDTO {
    private int qty;
    private int productId;
    private ProductGetDTO productGetDTO;

    public ShoppingCartItemGetDTO(ShoppingCartItem shoppingCartItem) {
        this.qty = shoppingCartItem.getQty();
        this.productId = shoppingCartItem.getProduct().getId();
        this.productGetDTO = new ProductGetDTO(shoppingCartItem.getProduct());
    }

}