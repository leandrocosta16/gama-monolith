package com.thesis.gama.dto;

import com.thesis.gama.model.Order;
import com.thesis.gama.model.OrderStatus;
import com.thesis.gama.model.ShoppingCart;
import com.thesis.gama.model.ShoppingCartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShoppingCartGetDTO {
    private int id;
    private List<ShoppingCartItemGetDTO> shoppingCartItemGetDTOS;

    public ShoppingCartGetDTO(ShoppingCart shoppingCart){
        this.id = shoppingCart.getId();

        if(shoppingCart.getShoppingCartItems() == null){
            this.shoppingCartItemGetDTOS = new ArrayList<>();
        }
        else {
            this.shoppingCartItemGetDTOS = shoppingCart.getShoppingCartItems().stream().map(ShoppingCartItemGetDTO::new).collect(Collectors.toList());
        }

    }
}
