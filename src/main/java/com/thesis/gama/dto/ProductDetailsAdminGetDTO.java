package com.thesis.gama.dto;

import com.thesis.gama.model.Inventory;
import com.thesis.gama.model.Product;
import com.thesis.gama.model.Promotion;
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
public class ProductDetailsAdminGetDTO {
    private int id;
    private String name;
    private String description;
    private double price;
    private boolean stock;
    private String brandName;
    private String categoryName;
    private int numOfRatings;
    private Double promotionPrice;
    private float averageRatingStars;
    private List<SpecificationValueGetDTO> specificationValues;
    private List<ReviewGetDTO> reviewGetDTOS;
    private List<InventoryGetDTO> inventories;
    private PromotionGetDTO promotion;

    public ProductDetailsAdminGetDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.promotionPrice = product.getPromotionPrice();
        this.stock = product.getStockAmount() > 0;
        this.brandName = product.getBrand().getName();
        this.categoryName = product.getCategory().getName();
        this.numOfRatings = product.getReviews().size();
        this.averageRatingStars = product.getAverageStarsRating();
        this.specificationValues = product.getSpecificationValues().stream().map(SpecificationValueGetDTO::new).collect(Collectors.toList());
        this.reviewGetDTOS = product.getReviews().stream().map(ReviewGetDTO::new).collect(Collectors.toList());
        this.inventories = product.getInventories().stream().map(InventoryGetDTO::new).collect(Collectors.toList());
        if(product.getPromotion()!=null) {
            this.promotion = new PromotionGetDTO(product.getPromotion());
        } else {
            this.promotion = null;
        }
    }
}
