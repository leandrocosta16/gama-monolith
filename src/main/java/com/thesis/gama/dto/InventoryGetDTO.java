package com.thesis.gama.dto;

import com.thesis.gama.model.Inventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryGetDTO {
    private String warehouseName;
    private String productName;
    private int stockAmount;

    public InventoryGetDTO(Inventory inventory) {
        this.warehouseName = inventory.getWarehouse().getName();
        this.productName = inventory.getProduct().getName();
        this.stockAmount = inventory.getStockAmount();
    }
}