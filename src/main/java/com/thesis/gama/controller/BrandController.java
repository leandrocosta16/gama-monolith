package com.thesis.gama.controller;

import com.thesis.gama.dto.BrandSetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.exceptions.ExistingForeignKeysException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.service.BrandService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/brands")
@Api(tags = "BrandController")
public class BrandController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private static final String BRAND_CREATED_LOG = "A Brand was created";
    private static final String BRAND_DELETED_LOG = "Brand: {} was deleted";
    private static final String BRAND_UPDATED_LOG = "Brand: {} was updated";

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }


    @Operation(summary = "Create a brand")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Brand created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "409", description = "Brand conflict", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void createBrand(@RequestBody BrandSetDTO brandSetDTO) throws AlreadyExistsException {
        this.brandService.createBrand(brandSetDTO);
        logger.info(BRAND_CREATED_LOG);
    }

    @Operation(summary = "Delete a brand")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand deleted", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path="/{brandId}")
    public void deleteBrand(@PathVariable int brandId) throws NoDataFoundException, ExistingForeignKeysException {
        this.brandService.deleteBrand(brandId);
        logger.info(BRAND_DELETED_LOG, brandId);
    }

}
