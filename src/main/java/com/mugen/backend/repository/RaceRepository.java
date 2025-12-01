package com.mugen.backend.repository;

import com.mugen.backend.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RaceRepository extends JpaRepository<Race, Integer> {

    Optional<Race> findByName(String name);

    List<Race> findByIsActiveTrue();

    boolean existsByName(String name);
}
