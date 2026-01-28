package com.malva_pastry_shop.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.malva_pastry_shop.backend.domain.sales.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // ========== Consultas Paginadas ==========

    Page<Sale> findAll(Pageable pageable);

    Page<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Sale> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    // ========== Consultas por Producto ==========

    List<Sale> findByProductId(Long productId);

    long countByProductId(Long productId);

    // ========== Consultas por Usuario ==========

    Page<Sale> findByRegisteredById(Long userId, Pageable pageable);

    // ========== Consultas por Fecha ==========

    List<Sale> findBySaleDateAfter(LocalDateTime date);

    List<Sale> findBySaleDateBefore(LocalDateTime date);
}
