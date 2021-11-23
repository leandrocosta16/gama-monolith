package com.thesis.gama.dto;

import com.thesis.gama.model.SpecificationValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecificationValueGetDTO {
    private String specificationName;
    private String specificationValue;

    public SpecificationValueGetDTO(SpecificationValue specificationValue){
        this.specificationName = specificationValue.getSpecificationName();
        this.specificationValue = specificationValue.getValue();
    }
}
