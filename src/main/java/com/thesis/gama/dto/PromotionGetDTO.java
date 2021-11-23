package com.thesis.gama.dto;

import com.thesis.gama.model.Promotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PromotionGetDTO {
    private String name;
    private String description;
    private int discountAmount;
    private Date startDate;
    private Date endDate;

    public PromotionGetDTO(Promotion promotion) {
        this.name = promotion.getName();
        this.description = promotion.getDescription();
        this.discountAmount = promotion.getDiscountAmount();
        this.startDate = promotion.getStartDate();
        this.endDate = promotion.getEndDate();
    }
}
