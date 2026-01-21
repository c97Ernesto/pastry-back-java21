package com.malva_pastry_shop.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.malva_pastry_shop.backend.domain.inventory.Tag;
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
        if (tagRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new IllegalArgumentException("Ya existe un tag activo con el nombre: " + request.getName());
        }

        // Generar slug único a partir del nombre
        String slug = generateUniqueSlug(request.getName(), null);

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setSlug(slug);
        tag.setDescription(request.getDescription());

        return tagRepository.save(tag);
    }

    // ========== Generación de Slug ==========

    /**
     * Genera un slug único a partir de un nombre.
     * Si ya existe, agrega un sufijo numérico.
     * 
     * @param name      Nombre del tag
     * @param excludeId ID del tag a excluir en la validación (para updates)
     * @return Slug único
     */
    private String generateUniqueSlug(String name, Long excludeId) {
        String baseSlug = SlugUtil.generateSlug(name);

        if (baseSlug == null || baseSlug.isBlank()) {
            throw new IllegalArgumentException("No se pudo generar un slug válido a partir del nombre");
        }

        String slug = baseSlug;
        int counter = 1;

        // Verificar unicidad y agregar sufijo si es necesario
        while (true) {
            boolean exists;

            if (excludeId != null) {
                // Para updates, excluir el propio tag
                exists = tagRepository.existsBySlugAndIdNotAndDeletedAtIsNull(slug, excludeId);
            } else {
                // Para creates
                exists = tagRepository.existsBySlugAndDeletedAtIsNull(slug);
            }

            if (!exists) {
                break;
            }

            counter++;
            slug = SlugUtil.generateUniqueSlug(baseSlug, counter);
        }

        return slug;
    }
}
