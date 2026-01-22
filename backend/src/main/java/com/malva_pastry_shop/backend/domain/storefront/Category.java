package com.malva_pastry_shop.backend.domain.storefront;

import com.malva_pastry_shop.backend.domain.common.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category extends SoftDeletableEntity {

    @NotBlank(message = "El nombre de la categoría es requerido")
    @Size(max = 100, message = "El nombre de la categoría no puede exceder los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String description;

    // ==================== CONSTRUCTORES ====================

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
