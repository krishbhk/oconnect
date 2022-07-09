package com.olacapstone.socialbackend.repository;

import java.util.List;
import java.util.Optional;

import com.olacapstone.socialbackend.entity.UserEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    Optional<List<UserEntity>> findAllByFirstName(String firstName);
    
}
