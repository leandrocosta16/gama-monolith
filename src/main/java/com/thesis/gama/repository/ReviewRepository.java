package com.thesis.gama.repository;

import com.thesis.gama.model.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Integer> {

    Optional<Review> findById(int id);

}
