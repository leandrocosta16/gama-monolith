package com.thesis.gama.repository;

import com.thesis.gama.model.Order;
import com.thesis.gama.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
    List<Order> findByUserOrderByBuyDate(User user);
    List<Order> findAll();
    Page<Order> findAll(Specification<Order> specs, Pageable pageable);
}