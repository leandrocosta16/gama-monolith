package com.thesis.gama.model;


import com.thesis.gama.dto.CategorySetDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String typicalSpecifications;


    public Category (CategorySetDTO categorySetDTO) {
        this.name = categorySetDTO.getName();
        this.typicalSpecifications = categorySetDTO.getTypicalSpecifications();
    }
}
