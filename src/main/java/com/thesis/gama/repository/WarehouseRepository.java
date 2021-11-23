package com.thesis.gama.repository;

import com.thesis.gama.model.Warehouse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends CrudRepository<Warehouse, Integer> {
    Optional<Warehouse> findByName(String name);
    Optional<Warehouse> findById(int id);
    

}
