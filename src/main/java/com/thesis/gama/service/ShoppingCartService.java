package com.thesis.gama.service;


import com.thesis.gama.dto.OrderItemSetDTO;
import com.thesis.gama.dto.OrderShoppingCartSetDTO;
import com.thesis.gama.dto.ShoppingCartGetDTO;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.exceptions.NoStockException;
import com.thesis.gama.model.ShoppingCart;
import com.thesis.gama.model.ShoppingCartItem;
import com.thesis.gama.model.User;
import com.thesis.gama.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class ShoppingCartService {

    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    ShoppingCartRepository shoppingCartRepository;
    @Autowired
    OrderService orderService;

    public void addItemToCart(String authorizationToken, OrderItemSetDTO orderItemSetDTO) throws NoDataFoundException {
        User user = this.userService.getMyUser(authorizationToken);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user);
        shoppingCart.addShoppingCartItem(new ShoppingCartItem(orderItemSetDTO, productService.getProductById(orderItemSetDTO.getProductId())));
        shoppingCartRepository.save(shoppingCart);
    }

    public void removeItemFromCart(String authorizationToken, int itemID) throws NoDataFoundException {
        User user = this.userService.getMyUser(authorizationToken);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user);
        shoppingCart.removeCartItem(itemID);
        shoppingCartRepository.save(shoppingCart);
    }



    public ShoppingCartGetDTO getShoppingCartOfUser(String authorizationToken){
        User user = this.userService.getMyUser(authorizationToken);
        return new ShoppingCartGetDTO(shoppingCartRepository.findByUser(user));
    }

    public void confirmShoppingCart(String authorizationToken, OrderShoppingCartSetDTO orderShoppingCartSetDTO) throws NoStockException, NoDataFoundException {
        User user = this.userService.getMyUser(authorizationToken);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user);
        orderService.createOrderFromShoppingCart(shoppingCart, orderShoppingCartSetDTO);
        shoppingCart.cleanCart();
        shoppingCartRepository.save(shoppingCart);
    }

    public void cleanShoppingCart(String authorizationToken) {
        User user = this.userService.getMyUser(authorizationToken);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user);
        shoppingCart.cleanCart();
        shoppingCartRepository.save(shoppingCart);
    }



}
