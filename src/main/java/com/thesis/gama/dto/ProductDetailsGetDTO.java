package com.thesis.gama.dto;

import com.thesis.gama.model.Product;
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
public class ProductDetailsGetDTO {
    private int id;
    private String name;
    private String description;
    private double price;
    private Double promotionPrice;
    private boolean stock;
    private String brandName;
    private String categoryName;
    private int numOfRatings;

    private float averageRatingStars;
    private List<SpecificationValueGetDTO> specificationValues;
    private List<ReviewGetDTO> reviewGetDTOS;

    public ProductDetailsGetDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.promotionPrice = product.getPrice();
        this.stock = product.getStockAmount() > 0;
        this.brandName = product.getBrand().getName();
        this.categoryName = product.getCategory().getName();
        this.numOfRatings = product.getReviews().size();
        this.averageRatingStars = product.getAverageStarsRating();
        this.specificationValues = product.getSpecificationValues().stream().map(SpecificationValueGetDTO::new).collect(Collectors.toList());
        this.reviewGetDTOS = product.getReviews().stream().map(ReviewGetDTO::new).collect(Collectors.toList());
    }
}
