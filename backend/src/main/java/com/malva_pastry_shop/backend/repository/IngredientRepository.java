package com.malva_pastry_shop.backend.repository;

import com.malva_pastry_shop.backend.domain.inventory.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    // Busqueda por nombre
    Page<Ingredient> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Buscar por nombre exacto
    Optional<Ingredient> findByName(String name);

    // Verificar nombre duplicado
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    // Listar todos ordenados por nombre
    List<Ingredient> findAllByOrderByNameAsc();
}