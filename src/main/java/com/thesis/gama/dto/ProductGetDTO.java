package com.thesis.gama.dto;

import com.thesis.gama.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductGetDTO {
    private int id;
    private String name;
    //private String description;
    private Double price;
    private Double promotionPrice;
    private boolean stock;
    private String brandName;
    private String categoryName;
    private int numOfRatings;
    private float averageRatingStars;
    //private List<SpecificationValueGetDTO> specificationValues;

    public ProductGetDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        //this.description = product.getDescription();
        this.price = product.getPrice();
        this.promotionPrice = product.getPromotionPrice();
        this.stock = product.getStockAmount() > 0;
        this.brandName = product.getBrand().getName();
        this.categoryName = product.getCategory().getName();
        this.numOfRatings = product.getReviews().size();
        this.averageRatingStars = product.getAverageStarsRating();
        //this.specificationValues = product.getSpecificationValues().stream().map(SpecificationValueGetDTO::new).collect(Collectors.toList());
    }
}