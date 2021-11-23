package com.thesis.gama.repository;

import com.thesis.gama.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByAccount_Email(String email);
    //User findByAccount_Email(String email);
}
