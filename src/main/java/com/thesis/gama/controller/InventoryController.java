package com.thesis.gama.controller;


import com.thesis.gama.dto.InventoryGetDTO;
import com.thesis.gama.dto.WarehouseGetDTO;
import com.thesis.gama.dto.WarehouseSetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.service.InventoryService;
import com.thesis.gama.exceptions.NoDataFoundException;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventories")
@Api(tags = "InventoryController")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private static final String ID = "inventoryId";
    private static final String INVENTORY_UPDATED_LOG = "Inventory updated for product {} and warehouse {}";
    private static final String WAREHOUSE_CREATED_LOG = "Warehouse: {} was created";
    private static final String WAREHOUSE_DELETED_LOG = "Warehouse: {} was deleted";
    private static final String WAREHOUSE_UPDATED_LOG = "Warehouse: {} was updated";


    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

/**
    @GetMapping
    public List<InventoryGetDTO> getInventoriesOfProduct(@RequestParam int product_id) {
        return this.inventoryService.;
    }

    @GetMapping
    public List<InventoryGetDTO> getInventoriesOfWarehouse(@RequestParam int warehouse_id) {
        return this.inventoryService.;
    }
**/

    //temporary
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/manual")
    public void editStock(
            @RequestParam("product_id") int product_id,
            @RequestParam("warehouse_id") int warehouse_id,
            @RequestParam("qty") int qty) throws NoDataFoundException {
        this.inventoryService.editStock(product_id, warehouse_id, qty);
    }

    //as responses unauthorized e forbidden sao auto explicativas nao preciso de description
    @Operation(summary = "Adds stock of a certain product to a certain warehouse")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Inventory was updated", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "404", description = "Product or Warehouse not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public void addStock(
            @RequestParam("product_id") int product_id,
            @RequestParam("warehouse_id") int warehouse_id,
            @RequestParam("qty") int qty) throws NoDataFoundException {

        this.inventoryService.addStock(product_id, warehouse_id, qty);
        //logger.info(INVENTORY_UPDATED_LOG, product_id, warehouse_id);
        //return ResponseEntity.ok(updatedInventory);
    }

    @Operation(summary = "Get all warehouses")
    @ApiResponse(responseCode = "200", description = "Warehouses found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path="/warehouses")
    public ResponseEntity<List<WarehouseGetDTO>> getAllWarehouses() {
        List<WarehouseGetDTO> warehouses = inventoryService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    //inventory of product_id=X, se bem que na lista de products do admin ja tem lá esta info, que posso reparoveitar no frontend, but still
    @Operation(summary = "Get all inventories of a product by its id")
    @ApiResponse(responseCode = "200", description = "Inventories found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @GetMapping
    public ResponseEntity<List<InventoryGetDTO>> getAllInventoriesOfProduct(@RequestParam("product_id") int product_id) {
        List<InventoryGetDTO> inventories = inventoryService.findInventoriesOfProduct(product_id);
        return ResponseEntity.ok(inventories);
    }


    @Operation(summary = "Create a warehouse")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Warehouse was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                            @ApiResponse(responseCode = "409", description = "There's a warehouse with given name", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path="/warehouses")
    public ResponseEntity<WarehouseGetDTO> createWarehouse(@RequestBody WarehouseSetDTO warehouseSetDTO) throws AlreadyExistsException {
        WarehouseGetDTO createdWarehouse = this.inventoryService.createWarehouse(warehouseSetDTO);
        logger.info(WAREHOUSE_CREATED_LOG, createdWarehouse);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWarehouse);
    }

    @Operation(summary = "Delete a warehouse")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Inventory was deleted", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = WarehouseGetDTO.class))}),
                            @ApiResponse(responseCode = "404", description = "Inventory not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path="/warehouses/{warehouseID}")
    public ResponseEntity<Integer> deleteWarehouse(@PathVariable int warehouseID) throws NoDataFoundException {
        this.inventoryService.deleteWarehouse(warehouseID);
        logger.info(WAREHOUSE_DELETED_LOG, warehouseID);
        return new ResponseEntity<>(warehouseID, HttpStatus.OK);
    }

    @Operation(summary = "Update a warehouse")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Inventory was updated", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", description = "Warehouse not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(path="/warehouses/{warehouseID}")
    public ResponseEntity<WarehouseGetDTO> editWarehouse(@RequestBody Map<String, Object> updates, @PathVariable("warehouseID") int id) throws NoDataFoundException {
        WarehouseGetDTO updatedWarehouse = this.inventoryService.editWarehouse(updates, id);
        logger.info(WAREHOUSE_UPDATED_LOG, updatedWarehouse);
        return ResponseEntity.ok(updatedWarehouse);
    }


}
