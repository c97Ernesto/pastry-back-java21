package com.malva_pastry_shop.backend.repository;

import com.malva_pastry_shop.backend.domain.inventory.ProductIngredient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Long> {

    // Ingredientes de un producto (con fetch de ingredient para evitar N+1)
    @EntityGraph(attributePaths = {"ingredient"})
    List<ProductIngredient> findByProductId(Long productId);

    // Productos que usan un ingrediente
    @EntityGraph(attributePaths = {"product"})
    List<ProductIngredient> findByIngredientId(Long ingredientId);

    // Contar productos que usan un ingrediente
    long countByIngredientId(Long ingredientId);

    // Verificar si un ingrediente esta en uso
    boolean existsByIngredientId(Long ingredientId);

    // Verificar si un producto tiene un ingrediente especifico
    boolean existsByProductIdAndIngredientId(Long productId, Long ingredientId);

    // Buscar un ingrediente especifico de un producto
    @EntityGraph(attributePaths = {"ingredient"})
    Optional<ProductIngredient> findByProductIdAndIngredientId(Long productId, Long ingredientId);

    // Eliminar todos los ingredientes de un producto
    void deleteByProductId(Long productId);
}
