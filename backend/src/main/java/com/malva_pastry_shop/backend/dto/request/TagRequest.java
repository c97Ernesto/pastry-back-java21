package com.malva_pastry_shop.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO unificado para crear y actualizar tags.
 */
@Getter
@Setter
@NoArgsConstructor
public class TagRequest {

    @NotBlank(message = "El nombre del tag es requerido")
    @Size(max = 50, message = "El nombre del tag no puede exceder 50 caracteres")
    private String name;

    @Size(max = 200, message = "La descripcion del tag no puede exceder 200 caracteres")
    private String description;
}
