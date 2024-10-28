package com.qa.dogs.data.repository;

import com.qa.dogs.data.BreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BreedRepository extends JpaRepository<BreedEntity, UUID> {
    Optional<BreedEntity> findByBreed(String bree);
}
