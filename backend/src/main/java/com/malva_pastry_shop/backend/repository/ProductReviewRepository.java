package com.malva_pastry_shop.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.malva_pastry_shop.backend.domain.publicuser.ProductReview;
import com.malva_pastry_shop.backend.domain.publicuser.ReviewStatus;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    // ========== Consultas Publicas (solo aprobadas) ==========

    @EntityGraph(attributePaths = { "publicUser" })
    Page<ProductReview> findByProductIdAndStatus(Long productId, ReviewStatus status, Pageable pageable);

    // ========== Consultas del Usuario ==========

    Optional<ProductReview> findByPublicUserIdAndProductId(Long publicUserId, Long productId);

    boolean existsByPublicUserIdAndProductId(Long publicUserId, Long productId);

    // ========== Consultas de Moderacion (Admin) ==========

    @EntityGraph(attributePaths = { "publicUser", "product" })
    Page<ProductReview> findByStatus(ReviewStatus status, Pageable pageable);

    long countByStatus(ReviewStatus status);
}
