package com.malva_pastry_shop.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO unificado para crear y actualizar categorías.
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "El nombre de la categoría es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
}
