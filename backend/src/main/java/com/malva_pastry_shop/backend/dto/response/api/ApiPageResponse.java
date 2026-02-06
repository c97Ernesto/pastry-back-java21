package com.malva_pastry_shop.backend.dto.response.api;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Respuesta paginada genérica para la API.
 * Envuelve los resultados de una consulta paginada.
 */
public record ApiPageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {
    /**
     * Crea una respuesta a partir de una Page de Spring Data.
     */
    public static <T> ApiPageResponse<T> from(Page<T> page) {
        return new ApiPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
