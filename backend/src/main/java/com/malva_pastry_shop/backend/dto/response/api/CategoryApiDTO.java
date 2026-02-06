package com.malva_pastry_shop.backend.dto.response.api;

/**
 * DTO para categorías en la API pública.
 */
public record CategoryApiDTO(
        Long id,
        String name,
        String description) {
}
