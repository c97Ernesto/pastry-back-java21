package com.malva_pastry_shop.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.malva_pastry_shop.backend.domain.sales.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // ========== Consultas Paginadas ==========

    Page<Sale> findAll(Pageable pageable);

    Page<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Sale> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    // ========== Consultas Combinadas (search + fecha) ==========

    @Query("SELECT s FROM Sale s WHERE LOWER(s.productName) LIKE LOWER(CONCAT('%', :name, '%')) AND s.saleDate BETWEEN :start AND :end")
    Page<Sale> findByProductNameAndDateRange(@Param("name") String name, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

    // ========== Consultas por Producto ==========

    List<Sale> findByProductId(Long productId);

    long countByProductId(Long productId);

    // ========== Consultas por Usuario ==========

    Page<Sale> findByRegisteredById(Long userId, Pageable pageable);

    // ========== Consultas por Fecha ==========

    List<Sale> findBySaleDateAfter(LocalDateTime date);

    List<Sale> findBySaleDateBefore(LocalDateTime date);

    // ========== Estadisticas ==========

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate >= :start AND s.saleDate < :end")
    long countBySaleDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate >= :start AND s.saleDate < :end")
    BigDecimal sumTotalAmountBySaleDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s")
    BigDecimal sumTotalAmount();

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTotalAmountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE LOWER(s.productName) LIKE LOWER(CONCAT('%', :name, '%'))")
    BigDecimal sumTotalAmountByProductNameContaining(@Param("name") String name);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE LOWER(s.productName) LIKE LOWER(CONCAT('%', :name, '%')) AND s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTotalAmountByProductNameAndDateRange(@Param("name") String name, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
