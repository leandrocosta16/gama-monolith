package com.thesis.gama.model;

import com.thesis.gama.dto.PromotionSetDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private int discountAmount;
    private Date startDate;
    private Date endDate;
    @Enumerated(EnumType.STRING)
    private PromotionState state;
    @OneToMany(mappedBy = "promotion")
    private List<Product> products;

    public Promotion(PromotionSetDTO promotionSetDTO) {
        this.name = promotionSetDTO.getName();
        this.description = promotionSetDTO.getDescription();
        this.discountAmount = promotionSetDTO.getDiscountAmount();
        this.startDate = promotionSetDTO.getStartDate();
        this.endDate = promotionSetDTO.getEndDate();
        this.state = PromotionState.INACTIVE;
        this.products = new ArrayList<>();
    }

    public void addProductToPromotion(Product product) {
        this.products.add(product);
        //product.setPromotion(this);
    }

    public void removeProductFromPromotion(Product product) {
        this.products.remove(product);
    }
}
