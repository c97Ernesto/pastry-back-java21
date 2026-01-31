package com.malva_pastry_shop.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.malva_pastry_shop.backend.domain.publicuser.PublicUser;

@Repository
public interface PublicUserRepository extends JpaRepository<PublicUser, Long> {

    Optional<PublicUser> findByGoogleId(String googleId);

    Optional<PublicUser> findByEmail(String email);

    boolean existsByGoogleId(String googleId);

    boolean existsByEmail(String email);
}
