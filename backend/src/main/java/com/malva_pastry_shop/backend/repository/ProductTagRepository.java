package com.malva_pastry_shop.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.malva_pastry_shop.backend.domain.storefront.ProductTag;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {

    List<ProductTag> findByProductId(Long productId);

    List<ProductTag> findByTagId(Long tagId);

    boolean existsByProductIdAndTagId(Long productId, Long tagId);

    Optional<ProductTag> findByProductIdAndTagId(Long productId, Long tagId);

    void deleteByProductId(Long productId);
}
