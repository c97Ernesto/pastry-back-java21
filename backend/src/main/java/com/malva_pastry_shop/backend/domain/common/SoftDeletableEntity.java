package com.malva_pastry_shop.backend.domain.common;

import com.malva_pastry_shop.backend.domain.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeletableEntity extends TimestampedEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_id")
    private User deletedBy;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete(User user) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = user;
    }

    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
