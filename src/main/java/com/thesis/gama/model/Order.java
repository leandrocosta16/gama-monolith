package com.thesis.gama.model;

import com.thesis.gama.dto.OrderSetDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="order_table")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date buyDate;

    @OneToMany(mappedBy = "order", cascade=CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private int idUser;

    //tem que ter address, os address do user so servem para dar para escolher um desses mas tem que haver um associado à order pq o user pode ter varios
    @OneToOne(cascade=CascadeType.ALL)
    private Shipping shipping;

    @OneToOne(cascade=CascadeType.ALL)
    private PaymentOrder paymentOrder;


    Double totalPrice;
    float totalWeight;

    public Order(OrderSetDTO orderSetDTO, User user) {
        this.buyDate = new Date(Calendar.getInstance().getTimeInMillis());
        this.orderStatus = OrderStatus.PENDING;
        this.user = user;
        this.idUser = user.getId();
        this.orderItems = new ArrayList<>();
    }

    public Order(ShoppingCart shoppingCart, User user) {
        this.buyDate = new Date(Calendar.getInstance().getTimeInMillis());
        this.orderStatus = OrderStatus.PENDING;
        this.user = user;
        this.idUser = user.getId();
    }



    public void setAllOrderItems(List<OrderItem> orderItems) {
        Double sumPrice = 0.0;
        float sumWeight = 0;
        for (OrderItem orderItem: orderItems) {
            orderItem.setOrder(this);
            sumPrice += orderItem.getPriceAtBuyTime() * orderItem.getQty();
            sumWeight += orderItem.getWeight() * orderItem.getQty();
        }
        this.orderItems = orderItems;
        this.totalPrice = sumPrice;
        this.totalWeight = sumWeight;
    }

    public void addShippingToOrder (Shipping shipping){
        shipping.setOrder(this);
        this.setShipping(shipping);
    }

    public void addPaymentToOrder (PaymentOrder paymentOrder){
        paymentOrder.setOrder(this);
        this.setPaymentOrder(paymentOrder);
    }

}
