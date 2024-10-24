package com.qa.dogs.data.repository;

import com.qa.dogs.data.DogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DogRepository extends JpaRepository<DogEntity, UUID> {
}
