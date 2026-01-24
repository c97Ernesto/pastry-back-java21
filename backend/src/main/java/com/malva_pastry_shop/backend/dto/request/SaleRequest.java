package com.malva_pastry_shop.backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para crear una venta.
 */
@Getter
@Setter
@NoArgsConstructor
public class SaleRequest {

    @NotNull(message = "El producto es requerido")
    private Long productId;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    @NotNull(message = "El precio unitario es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio unitario debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio unitario debe tener maximo 10 digitos enteros y 2 decimales")
    private BigDecimal unitPrice;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes;

    // ==================== DATOS DEL CLIENTE (Opcionales) ====================

    @Size(max = 150, message = "El nombre del cliente no puede exceder 150 caracteres")
    private String customerName;

    @Size(max = 20, message = "El DNI no puede exceder 20 caracteres")
    private String customerDni;

    @Size(max = 20, message = "El telefono no puede exceder 20 caracteres")
    private String customerPhone;
}
