package com.malva_pastry_shop.backend.dto.response.publicdto;

/**
 * DTO público para categorías expuestas en la API REST.
 */
public record CategoryPublicDTO(
        Long id,
        String name,
        String description) {
}
