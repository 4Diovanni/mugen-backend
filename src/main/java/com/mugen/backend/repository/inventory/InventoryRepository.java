package com.mugen.backend.repository.inventory;

import com.mugen.backend.entity.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para Inventory (Inventário)
 * Gerencia inventários dos personagens
 * ✅ 10 MÉTODOS IMPLEMENTADOS
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    // ==================== BUSCA BÁSICA ====================

    /**
     * Busca inventário por ID do personagem (1:1)
     * ✅ ESSENCIAL: Cada personagem tem 1 inventário
     */
    Optional<Inventory> findByCharacterId(UUID characterId);

    /**
     * Verifica se personagem tem inventário
     */
    boolean existsByCharacterId(UUID characterId);

    // ==================== QUERIES CUSTOMIZADAS ====================

    /**
     * Busca inventários que estão quase cheios (>80%)
     * Útil para notificar players
     */
    @Query("SELECT i FROM Inventory i WHERE (i.currentSlots * 100.0 / i.maxSlots) > 80 " +
            "ORDER BY (i.currentSlots * 100.0 / i.maxSlots) DESC")
    List<Inventory> findAlmostFullInventories();

    /**
     * Busca inventários cheios
     * Para limpar itens não usados
     */
    @Query("SELECT i FROM Inventory i WHERE i.currentSlots >= i.maxSlots " +
            "ORDER BY i.currentSlots DESC")
    List<Inventory> findFullInventories();

    /**
     * Busca inventários com mais valor em TP
     * Para análise de riqueza
     */
    @Query("SELECT i FROM Inventory i ORDER BY i.totalValue DESC")
    List<Inventory> findInventoriesByTotalValueDesc();

    /**
     * Busca inventários vazios
     * Para limpeza de dados
     */
    @Query("SELECT i FROM Inventory i WHERE i.currentSlots = 0")
    List<Inventory> findEmptyInventories();

    /**
     * Calcula valor total de todos os inventários
     * Para estatísticas globais
     */
    @Query("SELECT COALESCE(SUM(i.totalValue), 0) FROM Inventory i")
    long getTotalInventoriesValue();

    /**
     * Conta inventários
     * Para estatísticas
     */
    @Query("SELECT COUNT(i) FROM Inventory i")
    long countTotalInventories();

    /**
     * Busca inventários por utilização
     * Para otimizar espaço
     */
    @Query("SELECT i FROM Inventory i WHERE (i.currentSlots * 100.0 / i.maxSlots) BETWEEN :min AND :max")
    List<Inventory> findInventoriesByUsagePercentage(
            @Param("min") double min,
            @Param("max") double max
    );

    /**
     * Busca inventários com valor acima de X
     */
    @Query("SELECT i FROM Inventory i WHERE i.totalValue > :value ORDER BY i.totalValue DESC")
    List<Inventory> findInventoriesByMinValue(@Param("value") long value);
}
