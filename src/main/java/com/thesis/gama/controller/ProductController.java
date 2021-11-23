package com.thesis.gama.controller;

import com.sipios.springsearch.anotation.SearchSpec;
import com.thesis.gama.dto.*;
import com.thesis.gama.model.Product;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Api(tags = "ProductController")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private static final String PRODUCT_CREATED_LOG = "Product: {} was created";
    private static final String PRODUCT_DELETED_LOG = "Product: {} was deleted";
    private static final String PRODUCT_UPDATED_LOG = "Product: {} was updated";
    private static final String REVIEW_CREATED_LOG = "Review: {} was created";
    private static final String REVIEW_DELETED_LOG = "Review: {} was deleted";

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get a product by its id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Product found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @GetMapping(path="/{id}")
    public ResponseEntity<ProductDetailsGetDTO> getProductById(@PathVariable int id) throws NoDataFoundException {
        return ResponseEntity.ok(this.productService.getProductDetailsById(id));
    }

    @Operation(summary = "Get a collection of products")
    @ApiResponse(responseCode = "200", description = "Products found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @GetMapping
    public ResponseEntity<List<ProductGetDTO>> getAllProducts(
            @RequestParam(defaultValue = "desc,id") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @SearchSpec Specification<Product> specs) {

        return ResponseEntity.ok(this.productService.getProducts(page, size, sort, specs));
    }


    @Operation(summary = "Get a collection of products [ADMIN ONLY]")
    @ApiResponse(responseCode = "200", description = "Products found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/admin")
    public ResponseEntity<List<ProductDetailsAdminGetDTO>> getAllProductsAdmin(
            @RequestParam(defaultValue = "desc,id") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @SearchSpec Specification<Product> specs) {

        List<ProductDetailsAdminGetDTO> products = this.productService.getProductsAdmin(page, size, sort, specs);
        return ResponseEntity.ok(products);
    }


    @Operation(summary = "Create a product")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Product was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "404", description = "The provided Brand or Category does not exist", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductGetDTO> createProduct(@RequestHeader("Authorization") String authorizationToken, @RequestBody ProductSetDTO productSetDTO) throws NoDataFoundException {
        ProductGetDTO createdOrder = this.productService.createProduct(productSetDTO);
        logger.info(PRODUCT_CREATED_LOG, createdOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "Delete a product by its id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Order was deleted", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path="/{productID}")
    public ResponseEntity<Integer> deleteProduct(@RequestHeader("Authorization") String authorizationToken, @PathVariable int productID) throws NoDataFoundException {
        this.productService.deleteProduct(productID);
        logger.info(PRODUCT_DELETED_LOG, productID);
        return new ResponseEntity<>(productID, HttpStatus.OK);
    }

    @Operation(summary = "Get reviews of a given product")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Reviews found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @GetMapping(path="/{productID}/reviews")
    public ResponseEntity<List<ReviewGetDTO>> getProductReviews(@PathVariable int productID) throws NoDataFoundException {
        return ResponseEntity.ok(this.productService.getProductReviews(productID));
    }

    @Operation(summary = "Create a review of a given product")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Review created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @PostMapping(path="/{productID}/reviews")
    public ResponseEntity<ReviewGetDTO> createReview(@RequestHeader("Authorization") String authorizationToken, @PathVariable int productID, @RequestBody ReviewSetDTO reviewSetDTO) throws NoDataFoundException {
        ReviewGetDTO createdReview = this.productService.createReview(authorizationToken, productID, reviewSetDTO);
        logger.info(REVIEW_CREATED_LOG, createdReview);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @Operation(summary = "Delete a review by its id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Review was deleted", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)})
    @DeleteMapping(path="/{productID}/reviews/{reviewID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> deleteReview(@RequestHeader("Authorization") String authorizationToken, @PathVariable int productID, @PathVariable int reviewID) throws NoDataFoundException {
        this.productService.deleteReview(authorizationToken, reviewID);
        logger.info(REVIEW_DELETED_LOG, reviewID);
        return new ResponseEntity<>(reviewID, HttpStatus.OK);
    }

    @Operation(summary = "Update a product")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Product was updated", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(path="/{id}")
    public ResponseEntity<ProductGetDTO> editProduct(@RequestBody Map<String, Object> updates, @PathVariable("id") int id) throws NoDataFoundException {
        ProductGetDTO productUpdated = this.productService.editProduct(updates, id);
        logger.info(PRODUCT_UPDATED_LOG, productUpdated);
        return ResponseEntity.ok(productUpdated);
    }


}
