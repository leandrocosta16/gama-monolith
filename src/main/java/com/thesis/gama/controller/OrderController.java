package com.thesis.gama.controller;

import com.sipios.springsearch.anotation.SearchSpec;
import com.thesis.gama.dto.OrderGetDTO;
import com.thesis.gama.dto.OrderSetDTO;
import com.thesis.gama.dto.ShippingReferenceSetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.model.Order;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.exceptions.NoStockException;
import com.thesis.gama.model.Shipping;
import com.thesis.gama.service.OrderService;
import com.thesis.gama.service.ShippingService;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Api(tags = "OrderController")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private static final String ORDER_CREATED_LOG = "Order created";
    private static final String ORDER_DELETED_LOG = "Order: {} was deleted";

    private final OrderService orderService;
    private final ShippingService shippingService;

    @Autowired
    public OrderController(OrderService orderService, ShippingService shippingService) {
        this.orderService = orderService;
        this.shippingService = shippingService;
    }

    @Operation(summary = "Get all orders")
    @ApiResponse(responseCode = "200", description = "Orders found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path="/all") //por request params de de user_id optional, pagination, size, sort
    public ResponseEntity<List<OrderGetDTO>> getAllOrders(
            @RequestParam(defaultValue = "desc,id") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @SearchSpec Specification<Order> specs)  {
        return ResponseEntity.ok(this.orderService.getAllOrders(page, size, sort, specs));
    }

    @Operation(summary = "Get orders from authenticated user")
    @ApiResponse(responseCode = "200", description = "Orders found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @GetMapping
    public ResponseEntity<List<OrderGetDTO>> getUserOrders(@RequestHeader("Authorization") String authorizationToken) {
        List<OrderGetDTO> orders = this.orderService.getUserOrders(authorizationToken);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Create an order")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Order was created", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "409", description = "There is no stock available for one or more of the products selected", content = @Content),
            @ApiResponse(responseCode = "404", description = "Some of the selected products do not exist", content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@RequestHeader("Authorization") String authorizationToken, @RequestBody OrderSetDTO orderSetDTO) throws NoStockException, NoDataFoundException {
        this.orderService.createOrder(authorizationToken, orderSetDTO);
        logger.info(ORDER_CREATED_LOG);
        //return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    //if for some reason an order has to be deleted
    @Operation(summary = "Delete an order by its id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Order was deleted", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path="/{id}")
    public ResponseEntity<Integer> deleteOrder(@RequestHeader("Authorization") String authorizationToken, @PathVariable int id) throws NoDataFoundException {
        this.orderService.deleteOrder(authorizationToken, id);
        logger.info(ORDER_DELETED_LOG, id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }


    @PostMapping(path="/shipping")
    @ResponseStatus(HttpStatus.CREATED)
    public void createShippingReference(@RequestBody ShippingReferenceSetDTO shippingReferenceSetDTO) throws NoDataFoundException, AlreadyExistsException {
        this.shippingService.createShippingReference(shippingReferenceSetDTO);
    }

    @ExceptionHandler(NoStockException.class)
    protected ResponseEntity<Object> handleNoStockException(NoStockException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        final String path = request.getDescription(false);
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("timestamp", Instant.now());
        body.put("message", ex.getMessage());
        body.put("type", ex.getClass().getSimpleName());
        body.put("path", path);

        return new ResponseEntity<>(body, HttpStatus.CONFLICT); //mudar o status
    }


}