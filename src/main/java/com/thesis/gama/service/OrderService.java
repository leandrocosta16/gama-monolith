package com.thesis.gama.service;

import com.thesis.gama.dto.*;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.exceptions.NoStockException;
import com.thesis.gama.model.*;
import com.thesis.gama.repository.AddressRepository;
import com.thesis.gama.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class OrderService {

    private final UserService userService;
    private final ProductService productService;
    private final ShippingService shippingService;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public OrderService(UserService userService, ProductService productService, ShippingService shippingService, InventoryService inventoryService, OrderRepository orderRepository, AddressRepository addressRepository, ModelMapper modelMapper) {
        this.userService = userService;
        this.productService = productService;
        this.shippingService = shippingService;
        this.inventoryService = inventoryService;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }


    public Order getOrderById(int id) throws NoDataFoundException {

        return orderRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("There's no order with id " + id));
    }


    public List<OrderGetDTO> getUserOrders(String authorizationToken) {
        List<OrderGetDTO> userOrders = new ArrayList<>();
        User user = this.userService.getMyUser(authorizationToken);
        this.orderRepository.findByUserOrderByBuyDate(user).forEach(order -> userOrders.add(new OrderGetDTO(order)));
        return userOrders;
    }

    public List<OrderGetDTO> getAllOrders(int page, int size, String sort, org.springframework.data.jpa.domain.Specification<Order> specs) {

        String[] _sort = sort.split(",");

        Pageable paging = PageRequest.of(page, size, getSortDirection(_sort[0]), _sort[1]);

        List<OrderGetDTO> allOrders = new ArrayList<>();
        this.orderRepository.findAll(Specification.where(specs), paging).forEach(order -> allOrders.add(new OrderGetDTO(order)));
        return allOrders;
    }



    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }



    public void deleteOrder(String authorizationToken, int id) throws NoDataFoundException {
        if(orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            throw new NoDataFoundException ("There's no Warehouse with that id");
        }
    }

    public void createOrder(String authorizationToken, OrderSetDTO orderSetDTO) throws NoStockException, NoDataFoundException {
        User user = this.userService.getMyUser(authorizationToken);
        Order newOrder = new Order(orderSetDTO, user);

        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemSetDTO orderItemDTO : orderSetDTO.getOrderItems()){
            orderItems.add(new OrderItem(orderItemDTO, productService.getProductById(orderItemDTO.getProductId())));
        }
        try {
            inventoryService.reserveStock(orderItems);
        } catch (NoStockException e) {
            throw e;
        }
        newOrder.setAllOrderItems(orderItems);
        Shipping shipping = new Shipping(shippingService.calculateShippingValue(newOrder.getTotalWeight(), orderSetDTO.getCountry()), "notes", orderSetDTO.getAddress(), orderSetDTO.getCountry());
        newOrder.addShippingToOrder(shipping);
        orderRepository.save(newOrder);


    }




    public void createOrderFromShoppingCart(ShoppingCart shoppingCart, OrderShoppingCartSetDTO orderShoppingCartSetDTO) throws NoStockException, NoDataFoundException {
        User user = shoppingCart.getUser();
        Optional<Address> address = this.addressRepository.findById(orderShoppingCartSetDTO.getAddressID());
        if (address.isEmpty() || address.get().getUser() != user) {
            throw new NoDataFoundException("There's no address with id" + orderShoppingCartSetDTO.getAddressID());
        }
        Order newOrder = new Order(shoppingCart, user);

        List<OrderItem> orderItems = new ArrayList<>();
        for (ShoppingCartItem shoppingCartItem : shoppingCart.getShoppingCartItems()) {
            orderItems.add(new OrderItem(shoppingCartItem));
        }
        try {
            inventoryService.reserveStock(orderItems);
        } catch (NoStockException e) {
            throw e;
        }

        String fullAddress = address.get().getStreet() + address.get().getZipCode() + address.get().getCity();
        newOrder.setAllOrderItems(orderItems);
        Shipping shipping = new Shipping(shippingService.calculateShippingValue(newOrder.getTotalWeight(), address.get().getCountry()), "notes", fullAddress, address.get().getCountry());
        newOrder.addShippingToOrder(shipping);
        orderRepository.save(newOrder);
        System.out.println("order saved");

    }




    public void addPaymentToOrder(PaymentOrderSetDTO paymentOrderSetDTO) throws NoDataFoundException {
        Order order = this.getOrderById(paymentOrderSetDTO.getOrderID());
        PaymentOrder paymentOrder = new PaymentOrder(paymentOrderSetDTO, order.getTotalPrice() + order.getShipping().getCost());
        order.addPaymentToOrder(paymentOrder);
        orderRepository.save(order);
    }

    public void paymentSuccessful(int orderID) throws NoDataFoundException {
        Order order = this.getOrderById(orderID);
        order.setOrderStatus(OrderStatus.APPROVED);
        order.getPaymentOrder().setPayDate(new Date(Calendar.getInstance().getTimeInMillis()));
        order.getPaymentOrder().setState(PaymentStatus.PAYED);
        orderRepository.save(order);
    }


}


