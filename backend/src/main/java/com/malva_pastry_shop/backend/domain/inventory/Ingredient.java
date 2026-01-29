package com.malva_pastry_shop.backend.domain.inventory;

import java.math.BigDecimal;

import com.malva_pastry_shop.backend.domain.common.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
public class Ingredient extends SoftDeletableEntity {

    @NotBlank(message = "El nombre del ingrediente es requerido")
    @Size(max = 100, message = "El nombre del ingrediente no puede exceder los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La descripcion no puede exceder los 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "El costo unitario es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo unitario debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El costo unitario debe tener maximo 10 digitos enteros y 2 decimales")
    @Column(name = "unit_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost;

    @NotNull(message = "La unidad de medida es requerida")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private UnitOfMeasure unitOfMeasure;

    // ==================== CONSTRUCTORES ====================

    public Ingredient(String name, BigDecimal unitCost, UnitOfMeasure unitOfMeasure) {
        this.name = name;
        this.unitCost = unitCost;
        this.unitOfMeasure = unitOfMeasure;
    }
}
