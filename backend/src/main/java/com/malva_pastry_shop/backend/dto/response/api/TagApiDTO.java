package com.malva_pastry_shop.backend.dto.response.api;

/**
 * DTO para tags en la API pública.
 */
public record TagApiDTO(
        Long id,
        String name,
        String slug,
        String description) {
}
