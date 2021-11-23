package com.thesis.gama.dto;

import com.thesis.gama.model.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WarehouseGetDTO {
    private int id;
    private String name;
    private String description;

    public WarehouseGetDTO(Warehouse warehouse) {
        this.id = warehouse.getId();
        this.name = warehouse.getName();
        this.description = warehouse.getDescription();
    }
}