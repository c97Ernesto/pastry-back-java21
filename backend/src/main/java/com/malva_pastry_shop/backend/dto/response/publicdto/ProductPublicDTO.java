package com.malva_pastry_shop.backend.dto.response.publicdto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO público para productos expuestos en la API REST.
 * Contiene solo información visible para clientes.
 */
public record ProductPublicDTO(
        Long id,
        String name,
        String description,
        BigDecimal basePrice,
        Integer preparationDays,
        CategoryPublicDTO category,
        List<TagPublicDTO> tags) {
    /**
     * Versión simplificada sin categoría ni tags (para listados)
     */
    public record Simple(
            Long id,
            String name,
            BigDecimal basePrice,
            String categoryName) {
    }
}
