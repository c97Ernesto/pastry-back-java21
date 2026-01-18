package com.malva_pastry_shop.backend.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
public abstract class TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "inserted_at", nullable = false, updatable = false)
    private LocalDateTime insertedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * ID-based equals implementation following Hibernate best practices.
     * Uses Hibernate.getClass() to handle proxies correctly.
     * Returns false for transient entities (id == null).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        TimestampedEntity that = (TimestampedEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    /**
     * Returns a constant hashCode per entity class.
     * This ensures the hashCode doesn't change after persist(),
     * maintaining the contract for hash-based collections.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
