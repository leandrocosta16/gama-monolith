package com.thesis.gama.service;


import com.thesis.gama.dto.ProductDetailsAdminGetDTO;
import com.thesis.gama.dto.ProductReferenceSetDTO;
import com.thesis.gama.dto.PromotionGetDTO;
import com.thesis.gama.dto.PromotionSetDTO;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.exceptions.PromotionConflictException;
import com.thesis.gama.model.Product;
import com.thesis.gama.model.Promotion;
import com.thesis.gama.model.PromotionState;
import com.thesis.gama.repository.PromotionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    @Autowired
    public PromotionService(PromotionRepository promotionRepository, ProductService productService, ModelMapper modelMapper) {
        this.promotionRepository = promotionRepository;
        this.productService = productService;
        this.modelMapper= modelMapper;
    }


    public Promotion getPromotionById(int id) throws NoDataFoundException {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("There's no promotion with id " + id));
    }




    public PromotionGetDTO createPromotion(PromotionSetDTO promotionSetDTO) throws NoDataFoundException, PromotionConflictException {

        Promotion promotion = promotionRepository.save(new Promotion(promotionSetDTO));

        List<Product> products = new ArrayList<>();
        for(Integer pID : promotionSetDTO.getProductsIDs()){
            Product product = productService.getProductById(pID);
            products.add(product);
            productService.setPromotion(product, promotion); //verifies conflict
            products.add(product);
        }

        promotion.setProducts(products);
        promotionRepository.save(promotion);
        return modelMapper.map(promotion, PromotionGetDTO.class);
    }

    private boolean isProductAlreadyInAPromotion(Product product) {
        Optional<Promotion> matchingObject = promotionRepository.findActiveOrScheduledPromotions().stream().
                filter(p -> p.getProducts().contains(product)).
                findFirst();

        return matchingObject.isPresent();
    }


    public List<PromotionGetDTO> findAllPromotions() {
        List<PromotionGetDTO> allPromotions = new ArrayList<>();
        this.promotionRepository.findAll().forEach(promotion -> allPromotions.add(new PromotionGetDTO(promotion)));
        return allPromotions;
    }

    public void deletePromotion(int promotionID) throws NoDataFoundException {
        Promotion p = this.getPromotionById(promotionID);
        for(Product product : p.getProducts()) {
            productService.resetPromotionAndPrice(product);
        }
        promotionRepository.delete(p);
    }


    public void addProductsToPromotion(int promotionID, ProductReferenceSetDTO productReferenceSetDTO) throws NoDataFoundException, PromotionConflictException {
        Promotion promotion = this.getPromotionById(promotionID);
        List<Product> newProducts = new ArrayList<>();

        for (Integer pID : productReferenceSetDTO.getProductsIDs()) {
            Product product = productService.getProductById(pID);
            newProducts.add(product);
            productService.setPromotion(product, promotion);
        }
        promotion.setProducts(Stream.concat(promotion.getProducts().stream(), newProducts.stream())
                .collect(Collectors.toList()));
        promotionRepository.save(promotion);

    }

    public void removeProductsFromPromotion(int promotionID, ProductReferenceSetDTO productReferenceSetDTO) throws NoDataFoundException {
        Promotion promotion = this.getPromotionById(promotionID);

        for (Integer pID : productReferenceSetDTO.getProductsIDs()) {
            Product product = productService.getProductById(pID);
            if(promotion.getProducts().contains(product)) {
                promotion.getProducts().remove(product);
                productService.resetPromotionAndPrice(product);
                promotionRepository.save(promotion);
            }
        }
        promotionRepository.save(promotion);
    }

    //triggered by a scheduled job?
    public void endPromotion(int promotionID) throws NoDataFoundException {
        Promotion p = this.getPromotionById(promotionID);
        for(Product product : p.getProducts()){
            productService.resetPromotionAndPrice(product);
        }
        p.setState(PromotionState.EXPIRED);
        this.promotionRepository.save(p);
    }

    //triggered by a scheduled job?
    public void startPromotion(int promotionID) throws NoDataFoundException {
        Promotion p = this.getPromotionById(promotionID);
        for(Product product : p.getProducts()){
            productService.setPromotionPrice(product);
        }
        p.setState(PromotionState.ACTIVE);
        this.promotionRepository.save(p);
    }


    public List<ProductDetailsAdminGetDTO> getProductsOfPromotion(int promotionID) throws NoDataFoundException {
        List<ProductDetailsAdminGetDTO> allProducts = new ArrayList<>();
        Promotion promotion = this.getPromotionById(promotionID);
        promotion.getProducts().forEach(product -> allProducts.add(new ProductDetailsAdminGetDTO(product)));
        return allProducts;
    }
}
