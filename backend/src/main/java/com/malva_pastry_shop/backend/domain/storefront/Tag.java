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
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag extends SoftDeletableEntity {

    @NotBlank(message = "El nombre del tag es requerido")
    @Size(max = 50, message = "El nombre del tag no puede exceder 50 caracteres")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "El slug del tag es requerido")
    @Size(max = 100, message = "El slug del tag no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Size(max = 200, message = "La descripcion del tag no puede exceder 200 caracteres")
    @Column(length = 200)
    private String description;

    // ==================== CONSTRUCTORES ====================

    public Tag(String name) {
        this.name = name;
    }

    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
