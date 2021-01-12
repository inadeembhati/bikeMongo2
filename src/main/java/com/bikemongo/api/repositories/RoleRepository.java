package com.bikemongo.api.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bikemongo.api.model.RoleEnum;
import com.bikemongo.api.model.Role;

public interface RoleRepository extends MongoRepository<Role, String>{
    Optional<Role> findByName(RoleEnum roleName);
}
