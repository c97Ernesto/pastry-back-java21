package com.malva_pastry_shop.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.malva_pastry_shop.backend.domain.sales.SaleIngredient;

@Repository
public interface SaleIngredientRepository extends JpaRepository<SaleIngredient, Long> {

    // ========== Consultas por Venta ==========

    List<SaleIngredient> findBySaleId(Long saleId);

    // ========== Consultas por Ingrediente ==========

    List<SaleIngredient> findByIngredientId(Long ingredientId);

    long countByIngredientId(Long ingredientId);
}
