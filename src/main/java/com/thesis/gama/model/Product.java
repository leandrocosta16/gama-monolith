package com.thesis.gama.model;

import com.thesis.gama.dto.ProductSetDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private Double price;
    private Double promotionPrice;
    //private int stock;
    private float weight;

    @OneToOne
    private Brand brand;

    @OneToOne
    private Category category;

    @OneToMany(mappedBy = "product", cascade=CascadeType.ALL)
    private List<SpecificationValue> specificationValues;

    @OneToMany(mappedBy = "product", cascade=CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "product", cascade=CascadeType.ALL)
    private List<Inventory> inventories;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    Promotion promotion;

    public Product(ProductSetDTO productSetDTO, Brand brand, Category category) {
        this.name = productSetDTO.getName();
        this.description = productSetDTO.getDescription();
        this.price = productSetDTO.getPrice();
        this.promotionPrice = null; //tenho que criar um scheduled job para repor o promotion price quando atingir à data limite da promotion
        this.weight = productSetDTO.getWeight();
        //this.stock = productSetDTO.getStock();
        this.brand = brand;
        this.category = category;
        this.specificationValues = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.inventories = new ArrayList<>();
        this.promotion = null;
    }

    public int getStockAmount() {
        int totalStock = 0;
        for(Inventory i : inventories) {
            totalStock+=i.getStockAmount();
        }
        return totalStock;
    }

    public void addSpecificationValuesToProduct (List<SpecificationValue> specificationValues){
        for(SpecificationValue s : specificationValues) {
            s.setProduct(this);
        }
        this.setSpecificationValues(specificationValues);
    }

    public float getAverageStarsRating() {
        float i = 0.0f;
        float sum = 0.0f;
        if (!this.getReviews().isEmpty()) {
            for (Review r : this.getReviews()) {
                sum += r.getRatingStars();
                i++;
            }
            return sum / i;
        }
        return 0.0f;
    }
}
