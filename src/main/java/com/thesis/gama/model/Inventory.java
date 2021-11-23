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
@Table(name="inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    Warehouse warehouse;
    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    int stockAmount;

    public Inventory(Warehouse warehouse, Product product, int stockAmount) {
        this.warehouse = warehouse;
        this.product = product;
        this.stockAmount = stockAmount;
    }
}
