package com.thesis.gama.dto;

import com.thesis.gama.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewGetDTO {
    private int id;
    private String username;
    private int ratingStars;
    private String comment;

    public ReviewGetDTO(Review review){
        this.id = review.getId();
        this.username = review.getUser().getFirstName() + review.getUser().getLastName();
        this.ratingStars = review.getRatingStars();
        this.comment = review.getComment();
    }
}

