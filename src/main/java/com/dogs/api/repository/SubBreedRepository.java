package com.dogs.api.repository;

import com.dogs.api.model.SubBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubBreedRepository extends JpaRepository<SubBreed, Long> {

    boolean existsByNameIgnoreCaseAndBreedId(String name, Long breedId);
}
