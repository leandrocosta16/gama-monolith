package com.thesis.gama.repository;

import com.thesis.gama.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {

    Optional<Address> findById(int id);

}