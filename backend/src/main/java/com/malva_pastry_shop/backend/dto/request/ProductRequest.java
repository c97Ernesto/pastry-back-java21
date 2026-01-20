package com.malva_pastry_shop.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO unificado para crear y actualizar productos.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "El nombre del producto es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @Min(value = 0, message = "Los días de preparación deben ser >= 0")
    private Integer preparationDays;

    @DecimalMin(value = "0.0", message = "El precio debe ser >= 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal basePrice;

    private Long categoryId;
}
