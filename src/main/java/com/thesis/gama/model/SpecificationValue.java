package com.thesis.gama.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thesis.gama.dto.SpecificationValueGetDTO;
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
@Table(name="specification_value")
public class SpecificationValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;
    private String specificationName;
    private String value;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public SpecificationValue (SpecificationValueGetDTO specificationValueGetDTO) {
        this.specificationName = specificationValueGetDTO.getSpecificationName();
        this.value = specificationValueGetDTO.getSpecificationValue();
    }
}
