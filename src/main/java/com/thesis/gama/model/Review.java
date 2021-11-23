package com.thesis.gama.model;

import com.thesis.gama.dto.ReviewSetDTO;
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
@Table(name="review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int ratingStars;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Review(ReviewSetDTO reviewSetDTO, User user, Product product) {
        this.ratingStars = reviewSetDTO.getRatingStars();
        this.comment = reviewSetDTO.getComment();
        this.product = product;
        this.user = user;
    }
}
