package com.mugen.backend.repository;

import com.mugen.backend.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RaceRepository extends JpaRepository<Race, Integer> {

    // Buscar por nome
    Optional<Race> findByName(String name);

    // Verificar se nome existe
    boolean existsByName(String name);

    // Listar apenas raças ativas
    @Query("SELECT r FROM Race r WHERE r.isActive = true")
    List<Race> findAllActive();

    // Buscar raças ordenadas por nome
    List<Race> findAllByOrderByNameAsc();
}
