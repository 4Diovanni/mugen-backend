package com.mugen.backend.repository;

import com.mugen.backend.entity.TPTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TPTransactionRepository extends JpaRepository<TPTransaction, Long> {

    // Histórico de TP de um personagem
    @Query("SELECT t FROM TPTransaction t WHERE t.character.id = :characterId ORDER BY t.createdAt DESC")
    List<TPTransaction> findByCharacterIdOrderByCreatedAtDesc(@Param("characterId") UUID characterId);

    // Histórico filtrado por período
    @Query("SELECT t FROM TPTransaction t WHERE " +
            "t.character.id = :characterId AND " +
            "t.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY t.createdAt DESC")
    List<TPTransaction> findByCharacterIdAndDateRange(
            @Param("characterId") UUID characterId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Somar TP ganho/gasto por tipo
    @Query("SELECT SUM(t.amount) FROM TPTransaction t WHERE " +
            "t.character.id = :characterId AND " +
            "t.reason LIKE %:reasonPattern%")
    Long sumByCharacterIdAndReasonPattern(
            @Param("characterId") UUID characterId,
            @Param("reasonPattern") String reasonPattern
    );

    // Total de TP ganho (positivos)
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TPTransaction t WHERE " +
            "t.character.id = :characterId AND t.amount > 0")
    Long sumEarnedByCharacterId(@Param("characterId") UUID characterId);

    // Total de TP gasto (negativos)
    @Query("SELECT COALESCE(SUM(ABS(t.amount)), 0) FROM TPTransaction t WHERE " +
            "t.character.id = :characterId AND t.amount < 0")
    Long sumSpentByCharacterId(@Param("characterId") UUID characterId);
}
