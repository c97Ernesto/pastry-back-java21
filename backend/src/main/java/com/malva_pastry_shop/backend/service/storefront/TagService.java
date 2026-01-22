package com.malva_pastry_shop.backend.service.storefront;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.malva_pastry_shop.backend.domain.storefront.Tag;
import com.malva_pastry_shop.backend.dto.request.TagRequest;
import com.malva_pastry_shop.backend.repository.TagRepository;
import com.malva_pastry_shop.backend.util.SlugUtil;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // ========== Consultas ==========

    public Page<Tag> findAllActive(Pageable pageable) {
        return tagRepository.findByDeletedAtIsNull(pageable);
    }

    public Page<Tag> search(String name, Pageable pageable) {
        return tagRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(name, pageable);
    }

    // ========== CRUD ==========

    @Transactional
    public Tag create(TagRequest request) {
        validateTagName(request.getName(), null);

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setSlug(SlugUtil.generateSlug(request.getName()));
        tag.setDescription(request.getDescription());

        return tagRepository.save(tag);
    }

    // ========== Validaciones ==========

    /**
     * Valida que el nombre del tag sea único (case-insensitive).
     * Verifica tanto tags activos como en papelera.
     *
     * @param name      Nombre a validar
     * @param excludeId ID del tag a excluir (para updates), null para creates
     */
    private void validateTagName(String name, Long excludeId) {
        tagRepository.findByNameIgnoreCase(name).ifPresent(existingTag -> {
            // Si es update y es el mismo tag, no hay conflicto
            if (excludeId != null && existingTag.getId().equals(excludeId)) {
                return;
            }

            if (existingTag.isDeleted()) {
                throw new IllegalArgumentException(
                        "Ya existe un tag con el nombre '" + name + "' en la papelera. " +
                                "Puedes restaurarlo o eliminarlo permanentemente antes de crear uno nuevo.");
            } else {
                throw new IllegalArgumentException("Ya existe un tag con el nombre: " + name);
            }
        });
    }
}
