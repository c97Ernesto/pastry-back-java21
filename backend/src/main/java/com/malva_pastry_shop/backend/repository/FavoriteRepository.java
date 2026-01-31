package com.malva_pastry_shop.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.malva_pastry_shop.backend.domain.publicuser.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Favoritos de un usuario (con fetch de product para evitar N+1)
    @EntityGraph(attributePaths = {"product"})
    List<Favorite> findByPublicUserId(Long publicUserId);

    Optional<Favorite> findByPublicUserIdAndProductId(Long publicUserId, Long productId);

    boolean existsByPublicUserIdAndProductId(Long publicUserId, Long productId);

    void deleteByPublicUserIdAndProductId(Long publicUserId, Long productId);

    long countByProductId(Long productId);
}
