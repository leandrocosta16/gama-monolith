package com.thesis.gama.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductSetDTO {
    private String name;
    private String description;
    private Double price;
    private float weight;
    private int brandId;
    private int categoryId;
    private List<SpecificationValueGetDTO> specificationValues; //o get serve
}
