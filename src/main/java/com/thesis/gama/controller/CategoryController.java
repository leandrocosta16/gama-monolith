package com.thesis.gama.controller;

import com.thesis.gama.dto.CategorySetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.exceptions.ExistingForeignKeysException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.service.CategoryService;
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
@RequestMapping("/categories")
@Api(tags = "CategoryController")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private static final String CATEGORY_CREATED_LOG = "A Category was created";
    private static final String CATEGORY_DELETED_LOG = "Category: {} was deleted";
    private static final String CATEGORY_UPDATED_LOG = "Category: {} was updated";

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @Operation(summary = "Create a category")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Category created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "409", description = "Category conflict", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void createCategory(@RequestBody CategorySetDTO categorySetDTO) throws AlreadyExistsException {
        this.categoryService.createCategory(categorySetDTO);
        logger.info(CATEGORY_CREATED_LOG);
    }

    @Operation(summary = "Delete a category")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path="/{catId}")
    public void deleteCategory(@PathVariable int catId) throws NoDataFoundException, ExistingForeignKeysException {
        this.categoryService.deleteCategory(catId);
        logger.info(CATEGORY_DELETED_LOG, catId);
    }

}