package com.malva_pastry_shop.backend.repository;

import com.malva_pastry_shop.backend.domain.inventory.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // ========== Consultas activos (no eliminados) ==========

    Page<Tag> findByDeletedAtIsNull(Pageable pageable);

    Page<Tag> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);

    Optional<Tag> findByIdAndDeletedAtIsNull(Long id);

    List<Tag> findByDeletedAtIsNullOrderByNameAsc();

    // ========== Consultas papelera (eliminados) ==========

    Page<Tag> findByDeletedAtIsNotNull(Pageable pageable);

    // ========== Validaciones de nombre unico ==========

    boolean existsByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, Long id);

    // ========== Validaciones de slug unico ==========

    boolean existsBySlugAndDeletedAtIsNull(String slug);

    boolean existsBySlugAndIdNotAndDeletedAtIsNull(String slug, Long id);

    // ========== Busqueda por IDs ==========

    List<Tag> findByIdInAndDeletedAtIsNull(List<Long> ids);
}
