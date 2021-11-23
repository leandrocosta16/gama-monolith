package com.thesis.gama.controller;


import com.thesis.gama.dto.ProductDetailsAdminGetDTO;
import com.thesis.gama.dto.ProductReferenceSetDTO;
import com.thesis.gama.dto.PromotionGetDTO;
import com.thesis.gama.dto.PromotionSetDTO;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.exceptions.PromotionConflictException;
import com.thesis.gama.service.PromotionService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/promotions")
@Api(tags = "PromotionController")
public class PromotionController {

    private static final Logger logger = LoggerFactory.getLogger(PromotionController.class);

    private static final String PROMOTION_CREATED_LOG = "Promotion: {} was created";
    private static final String PROMOTION_DELETED_LOG = "Promotion: {} was deleted";
    private static final String PRODUCT_UPDATED_LOG = "Product: {} was updated";

    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @Operation(summary = "Get all promotions")
    @ApiResponse(responseCode = "200", description = "Promotions found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @GetMapping
    public ResponseEntity<List<PromotionGetDTO>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.findAllPromotions());
    }

    @Operation(summary = "Deletes an existing promotion")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Promotion was deleted", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", description = "Promotion not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Integer> deletePromotion(@PathVariable int id) throws NoDataFoundException {
        promotionService.deletePromotion(id);
        logger.info(PROMOTION_DELETED_LOG, id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @Operation(summary = "Get  products from old promotions [ADMIN ONLY]")
    @ApiResponse(responseCode = "200", description = "Promotions found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/{id}/products")
    public ResponseEntity<List<ProductDetailsAdminGetDTO>> getProductsOfPromotion(@PathVariable int id) throws NoDataFoundException {
        return ResponseEntity.ok(promotionService.getProductsOfPromotion(id));
    }

    @Operation(summary = "Create a promotion")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Promotion was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "404", description = "Some product(s) does not exist", content = @Content),
                            @ApiResponse(responseCode = "409", description = "Some product(s) does not exist", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PromotionGetDTO> createPromotion(@RequestBody PromotionSetDTO promotionSetDTO) throws NoDataFoundException, PromotionConflictException {
        promotionService.createPromotion(promotionSetDTO);

        PromotionGetDTO createdPromotion = promotionService.createPromotion(promotionSetDTO);
        logger.info(PROMOTION_CREATED_LOG, createdPromotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/{id}/products")
    public void addProductToPromotion(@PathVariable int id, @RequestBody ProductReferenceSetDTO productReferenceSetDTO) throws NoDataFoundException, PromotionConflictException {
        promotionService.addProductsToPromotion(id, productReferenceSetDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{promotion_id}/products/{product_id}")
    public void removeProductFromPromotion(@PathVariable int promotion_id, @RequestBody ProductReferenceSetDTO productReferenceSetDTO) throws NoDataFoundException, PromotionConflictException {
        promotionService.removeProductsFromPromotion(promotion_id, productReferenceSetDTO);
    }


    @ExceptionHandler(PromotionConflictException.class)
    protected ResponseEntity<Object> handlePromotionConflictException(PromotionConflictException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        final String path = request.getDescription(false);
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("timestamp", Instant.now());
        body.put("message", ex.getMessage());
        body.put("type", ex.getClass().getSimpleName());
        body.put("path", path);

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


}
