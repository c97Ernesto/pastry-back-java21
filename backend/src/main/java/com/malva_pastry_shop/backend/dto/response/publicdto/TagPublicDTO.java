package com.malva_pastry_shop.backend.dto.response.publicdto;

/**
 * DTO público para tags expuestos en la API REST.
 * Incluye slug para URLs amigables.
 */
public record TagPublicDTO(
        Long id,
        String name,
        String slug,
        String description) {
}
