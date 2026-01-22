package com.malva_pastry_shop.backend.repository;

import com.malva_pastry_shop.backend.domain.storefront.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Categorías activas (no eliminadas)
    Page<Category> findByDeletedAtIsNull(Pageable pageable);

    // Búsqueda por nombre (solo activas)
    Page<Category> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);

    // Categoría activa por ID
    Optional<Category> findByIdAndDeletedAtIsNull(Long id);

    // Categorías eliminadas (papelera)
    Page<Category> findByDeletedAtIsNotNull(Pageable pageable);

    // Validacion de nombre unico (case-insensitive)
    Optional<Category> findByNameIgnoreCase(String name);
}
