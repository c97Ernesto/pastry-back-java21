package com.malva_pastry_shop.backend.repository;

import com.malva_pastry_shop.backend.domain.inventory.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Productos activos (no eliminados)
    Page<Product> findByDeletedAtIsNull(Pageable pageable);

    // Productos activos por categoria
    Page<Product> findByCategoryIdAndDeletedAtIsNull(Long categoryId, Pageable pageable);

    // Busqueda por nombre (solo activos)
    Page<Product> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);

    // Producto activo por ID
    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    // Contar productos por categoria
    long countByCategoryId(Long categoryId);

    // Contar productos activos por categoria
    long countByCategoryIdAndDeletedAtIsNull(Long categoryId);

    // Verificar nombre duplicado
    boolean existsByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, Long id);

    // Productos eliminados (papelera)
    Page<Product> findByDeletedAtIsNotNull(Pageable pageable);
}
