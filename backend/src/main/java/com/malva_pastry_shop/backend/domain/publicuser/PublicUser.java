package com.malva_pastry_shop.backend.domain.publicuser;

import com.malva_pastry_shop.backend.domain.common.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "public_users")
@Getter
@Setter
@NoArgsConstructor
public class PublicUser extends TimestampedEntity {

    @NotBlank(message = "El ID de Google es requerido")
    @Column(name = "google_id", nullable = false, unique = true, length = 255)
    private String googleId;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Size(max = 500, message = "La URL del avatar no puede exceder 500 caracteres")
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @NotNull
    @Column(nullable = false)
    private Boolean enabled = true;

    public PublicUser(String googleId, String email, String displayName, String avatarUrl) {
        this.googleId = googleId;
        this.email = email;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    @Override
    public String toString() {
        return "PublicUser [id=" + getId()
                + ", email=" + email
                + ", displayName=" + displayName + "]";
    }
}
