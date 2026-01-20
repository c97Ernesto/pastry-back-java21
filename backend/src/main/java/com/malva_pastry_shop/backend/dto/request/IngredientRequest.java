package com.malva_pastry_shop.backend.dto.request;

import java.math.BigDecimal;

import com.malva_pastry_shop.backend.domain.inventory.UnitOfMeasure;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO unificado para crear y actualizar ingredientes.
 */
@Getter
@Setter
@NoArgsConstructor
public class IngredientRequest {

    @NotBlank(message = "El nombre del ingrediente es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
    private String description;

    @NotNull(message = "El costo unitario es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo unitario debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El costo unitario debe tener maximo 10 digitos enteros y 2 decimales")
    private BigDecimal unitCost;

    @NotNull(message = "La unidad de medida es requerida")
    private UnitOfMeasure unitOfMeasure;
}
