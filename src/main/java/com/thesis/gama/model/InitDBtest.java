package com.thesis.gama.model;
/**
import com.thesis.gama.dto.CategorySetDTO;
import com.thesis.gama.dto.ProductSetDTO;
import com.thesis.gama.dto.SpecificationValueGetDTO;
import com.thesis.gama.dto.UserSetDTO;
import com.thesis.gama.repository.ShippingRepository;
import com.thesis.gama.repository.WarehouseRepository;
import com.thesis.gama.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InitDBtest {
    private static final Logger log = LoggerFactory.getLogger(InitDBtest.class);

    @Bean
    CommandLineRunner initDatabase(UserService userService, ProductService productService, BrandService brandService, CategoryService categoryService, WarehouseRepository warehouseRepository, InventoryService inventoryService, ShippingRepository shippingRepository) {

        return args -> {
            log.info("Preloading ");

            Warehouse w1 = new Warehouse("W1", "One warehouse");
            warehouseRepository.save(w1);
            Warehouse w2 = new Warehouse("W2", "Two warehouse");
            warehouseRepository.save(w2);

            ShippingReferenceValue s1 = new ShippingReferenceValue(5.0, "Portugal");
            shippingRepository.save(s1);

            brandService.createBrand("Sony");

            CategorySetDTO category1 = new CategorySetDTO("Consolas", "lol");
            categoryService.createCategory(category1);

            CategorySetDTO category2 = new CategorySetDTO("Jogos PS4", "lol");
            categoryService.createCategory(category2);

            userService.createAdmin(new UserSetDTO("string", "string", "string", "string", null, "string", "string", "string", "string", "string", "string" ));

            productService.createProduct(new ProductSetDTO("PS4", "description", 250.0, 2f, 1, 1, new ArrayList<SpecificationValueGetDTO>()));
            productService.createProduct(new ProductSetDTO("PS5", "description", 499.0, 3f, 1, 1, new ArrayList<SpecificationValueGetDTO>()));


            productService.createProduct(new ProductSetDTO("Uncharted", "description", 19.00, 0.5f, 1, 2, new ArrayList<SpecificationValueGetDTO>()));
            productService.createProduct(new ProductSetDTO("The Last of Us", "description", 20.0, 0.4f, 1, 2, new ArrayList<SpecificationValueGetDTO>()));

            inventoryService.addStock(1,1, 5);
            inventoryService.addStock(1,2, 10);


        };
    }
}
**/