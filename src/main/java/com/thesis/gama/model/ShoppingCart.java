package com.thesis.gama.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "shoppingcart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "shoppingcart", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ShoppingCartItem> shoppingCartItems;

    @OneToOne(mappedBy = "shoppingcart")
    private User user;


    public ShoppingCart(User user) {
        this.shoppingCartItems = new ArrayList<>();
        this.user = user;
    }

    public void addShoppingCartItem(ShoppingCartItem newShoppingCartItem) {

        Optional<ShoppingCartItem> matchingObject = shoppingCartItems.stream().
                filter(p -> p.getProduct().getId() == newShoppingCartItem.getProduct().getId()).
                findFirst();
        if(matchingObject.isPresent()) {
            matchingObject.get().setQty(matchingObject.get().getQty() + newShoppingCartItem.getQty());
        } else {
            newShoppingCartItem.setShoppingcart(this);
            shoppingCartItems.add(newShoppingCartItem);
        }

    }

    public void removeCartItem(int toRemoveID) { //se o objeto item viesse da db podia fazer logo .remove(item)?


        shoppingCartItems.removeIf(item -> item.getProduct().getId() == toRemoveID);


/**
 for (int y = 0; y < shoppingCartItems.size(); ++y) {
 ShoppingCartItem item = shoppingCartItems.get(y);
 if(item.getId() == toRemoveID) {
 shoppingCartItems.remove(item);
 return;
 }
 }
 **/
    }

    public void cleanCart() {
        this.shoppingCartItems.clear();
    }

}