package com.thesis.gama.repository;

import com.thesis.gama.model.ShoppingCart;
import com.thesis.gama.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Integer> {
    ShoppingCart findByUser(User user);
}
