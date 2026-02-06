package com.malva_pastry_shop.backend.dto.response.api;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para productos en la API pública.
 * Contiene información básica y detallada del producto.
 */
public record ProductApiDTO(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        Integer preparationDays,
        String imageUrl,
        CategoryApiDTO category,
        List<TagApiDTO> tags) {
    /**
     * Versión simplificada para listados.
     */
    public record Simple(
            Long id,
            String name,
            BigDecimal basePrice,
            String imageUrl,
            String categoryName) {
    }
}
