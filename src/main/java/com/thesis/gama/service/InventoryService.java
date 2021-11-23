package com.thesis.gama.service;

import com.thesis.gama.dto.InventoryGetDTO;
import com.thesis.gama.dto.WarehouseGetDTO;
import com.thesis.gama.dto.WarehouseSetDTO;
import com.thesis.gama.exceptions.AlreadyExistsException;
import com.thesis.gama.exceptions.NoDataFoundException;
import com.thesis.gama.exceptions.NoStockException;
import com.thesis.gama.model.Inventory;
import com.thesis.gama.model.OrderItem;
import com.thesis.gama.model.Warehouse;
import com.thesis.gama.repository.InventoryRepository;
import com.thesis.gama.repository.WarehouseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class InventoryService {


    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    public InventoryService(InventoryRepository inventoryRepository, WarehouseRepository warehouseRepository, ProductService productService, ModelMapper modelMapper) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    public List<InventoryGetDTO> findInventoriesOfProduct(int productID) {
        //return inventoryRepository.findAllByProductId(productID);
        List<InventoryGetDTO> allInventories = new ArrayList<>();
        this.inventoryRepository.findAllByProductId(productID).forEach(inventory -> allInventories.add(modelMapper.map(inventory, InventoryGetDTO.class)));
        return allInventories;
    }

    public List<Inventory> findInventoriesByWarehouse(int warehouseID) {
        return inventoryRepository.findAllByWarehouseId(warehouseID);
    }


    public void addStock(int productID, int warehouseID, int qty) throws NoDataFoundException {
        Optional<Inventory> inventory = inventoryRepository.findByProductIdAndWarehouseId(productID, warehouseID);
        if (inventory.isPresent()) {
            inventory.get().setStockAmount(inventory.get().getStockAmount() + qty);
            //return modelMapper.map(inventory.get(), InventoryGetDTO.class);
        } else {
            Warehouse w = this.getWarehouseById(warehouseID);
            Inventory newInv = new Inventory(w, productService.getProductById(productID), qty);
            inventoryRepository.save(newInv);
            //return modelMapper.map(newInv, InventoryGetDTO.class);
        }
    }

    public void editStock(int productID, int warehouseID, int qty) throws NoDataFoundException {
        Optional<Inventory> inventory = inventoryRepository.findByProductIdAndWarehouseId(productID, warehouseID);
        if (inventory.isPresent()) {
            inventory.get().setStockAmount(qty);
            inventoryRepository.save(inventory.get());
        } else {
            throw new NoDataFoundException("There's no inventory for Product with id " + productID + " and warehouse with id" + warehouseID);
        }
    }



    public void reserveStock(List<OrderItem> items) throws NoStockException, NoDataFoundException {
        //loop a ver se todos têm stock e outro loop a retirar um e guardar
/**
        boolean hasStock = items.stream().anyMatch( i -> {
            try {
                return productService.getProductById(i.getProductId()).getStockAmount() < i.getQty();
            } catch (NoDataFoundException e) {
                e.printStackTrace();
                return false;
            }
        });
        **/


        for (OrderItem o : items) {
            if (productService.getProductById(o.getProductId()).getStockAmount() < o.getQty()) {
                throw new NoStockException("Not that much stock available for that product");
            }
        }
        for (OrderItem o : items) {
            int qtyLeft = o.getQty();
            for (int y = 0; y < productService.getProductById(o.getProductId()).getInventories().size(); ++y) { //teve que ser loop assim pq com iterator na podia modificar elemento enquanto iterava
                if (qtyLeft == 0) {
                    return;
                }
                Inventory i = productService.getProductById(o.getProductId()).getInventories().get(y);
                if (qtyLeft <= i.getStockAmount()) {
                    i.setStockAmount(i.getStockAmount() - qtyLeft);
                    qtyLeft = 0;
                } else {
                    qtyLeft = qtyLeft - i.getStockAmount();
                    i.setStockAmount(0);
                }
                this.inventoryRepository.save(i);
            }

        }

    }

    public List<WarehouseGetDTO> getAllWarehouses() { //usado quando quero escolher em qual warehouse
        List<WarehouseGetDTO> allWarehouses = new ArrayList<>();

        //method 1
        //this.warehouseRepository.findAll().forEach(warehouse -> allWarehouses.add(new WarehouseGetDTO(warehouse)));
        //method 2
        this.warehouseRepository.findAll().forEach(warehouse -> allWarehouses.add(modelMapper.map(warehouse, WarehouseGetDTO.class)));

        return allWarehouses;
    }

    public WarehouseGetDTO createWarehouse(WarehouseSetDTO warehouseSetDTO) throws AlreadyExistsException {

        Optional<Warehouse> existingWarehouse = this.warehouseRepository.findByName(warehouseSetDTO.getName());
        if (existingWarehouse.isEmpty()) {
            Warehouse warehouse = new Warehouse((warehouseSetDTO));
            warehouseRepository.save(warehouse);
            return modelMapper.map(warehouse, WarehouseGetDTO.class);
        } else {
            throw new AlreadyExistsException("There's a Warehouse with that name");
        }
    }


    public void deleteWarehouse(int id) throws NoDataFoundException {

        if (warehouseRepository.existsById(id)) { //evita que tenha de fazer um fetch extra
            warehouseRepository.deleteById(id);
        } else {
            throw new NoDataFoundException("There's no Warehouse with that id");
        }
    }

    public WarehouseGetDTO editWarehouse(Map<String, Object> updates, int id) throws NoDataFoundException {
        Optional<Warehouse> warehouse = warehouseRepository.findById(id);
        if (warehouse.isPresent()) {
            try {
                // Map key is field name, v is value
                updates.forEach((k, v) -> {
                    // use reflection to get field k on manager and set it to value v
                    Field field = ReflectionUtils.findField(Warehouse.class, k);
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, warehouse.get(), v);
                });
                warehouseRepository.save(warehouse.get());
                return modelMapper.map(warehouse.get(), WarehouseGetDTO.class);
            } catch (Exception e) {
                throw new NoDataFoundException("Invalid arguments");
            }
        } else {
            throw new NoDataFoundException("There's no Warehouse with that id");
        }

    }


    public Warehouse getWarehouseById(int id) throws NoDataFoundException {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("There's no Warehouse with id: " + id));
    }
}
