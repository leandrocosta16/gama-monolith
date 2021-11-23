package com.thesis.gama.repository;

import com.thesis.gama.model.SpecificationValue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecificationValueRepository extends CrudRepository<SpecificationValue, Integer> {

}
