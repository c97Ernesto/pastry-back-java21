package com.malva_pastry_shop.backend.repository;

import com.malva_pastry_shop.backend.domain.inventory.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Long> {

    // Ingredientes de un producto
    List<ProductIngredient> findByProductId(Long productId);

    // Productos que usan un ingrediente
    List<ProductIngredient> findByIngredientId(Long ingredientId);

    // Contar productos que usan un ingrediente
    long countByIngredientId(Long ingredientId);

    // Verificar si un ingrediente esta en uso
    boolean existsByIngredientId(Long ingredientId);

    // Eliminar todos los ingredientes de un producto
    void deleteByProductId(Long productId);
}