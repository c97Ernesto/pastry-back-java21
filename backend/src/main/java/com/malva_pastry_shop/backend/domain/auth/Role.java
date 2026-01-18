package com.malva_pastry_shop.backend.domain.auth;

import com.malva_pastry_shop.backend.domain.common.TimestampedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends TimestampedEntity {

    @NotNull(message = "El tipo de rol es requerido")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String description;

    public Role(RoleType name) {
        this.name = name;
    }

    public Role(RoleType name, String description) {
        this.name = name;
        this.description = description;
    }
}
