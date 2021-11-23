package com.thesis.gama.controller;


import com.thesis.gama.dto.OrderItemSetDTO;
import com.thesis.gama.dto.OrderShoppingCartSetDTO;
import com.thesis.gama.dto.ShoppingCartGetDTO;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.exceptions.NoStockException;
import com.thesis.gama.service.ShoppingCartService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Api(tags = "ShoppingCartController")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartGetDTO getUserShoppingCart(@RequestHeader("Authorization") String authorizationToken) {
        return this.shoppingCartService.getShoppingCartOfUser(authorizationToken);
    }

    @PostMapping
    public void addItemToShoppingCart(@RequestHeader("Authorization") String authorizationToken, @RequestBody OrderItemSetDTO orderItemSetDTO) throws NoDataFoundException {
        this.shoppingCartService.addItemToCart(authorizationToken, orderItemSetDTO);
    }

    @DeleteMapping
    public void removeItemFromCart(@RequestHeader("Authorization") String authorizationToken, @RequestParam int product_id) throws NoDataFoundException {
        this.shoppingCartService.removeItemFromCart(authorizationToken, product_id);
    }

    @PostMapping(path="/confirm")
    public void confirmCartAsOrder(@RequestHeader("Authorization") String authorizationToken, @RequestBody OrderShoppingCartSetDTO orderShoppingCartSetDTO) throws NoDataFoundException, NoStockException {
        this.shoppingCartService.confirmShoppingCart(authorizationToken, orderShoppingCartSetDTO);
    }

    @DeleteMapping(path="/clean")
    public void cleanCart(@RequestHeader("Authorization") String authorizationToken) {
        this.shoppingCartService.cleanShoppingCart(authorizationToken);
    }

}
