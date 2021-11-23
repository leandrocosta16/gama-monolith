package com.thesis.gama.model;

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
@Table(name="shipping")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double cost;
    private String notes;
    @OneToOne(mappedBy = "shipping")
    private Order order;
    //@OneToOne
    //private Address address;
    private String country;
    private String address;

    public Shipping(Double cost, String notes, String address, String country) {
        this.cost = cost;
        this.notes = notes;
        this.address = address;
        this.country = country;
    }
}
