package com.malva_pastry_shop.backend.config;

import com.malva_pastry_shop.backend.domain.auth.Role;
import com.malva_pastry_shop.backend.domain.auth.RoleType;
import com.malva_pastry_shop.backend.domain.auth.User;
import com.malva_pastry_shop.backend.repository.RoleRepository;
import com.malva_pastry_shop.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        for (RoleType roleType : RoleType.values()) {
            if (!roleRepository.existsByName(roleType)) {
                Role role = new Role(roleType, getDescriptionFor(roleType));
                roleRepository.save(role);
                log.info("Rol creado: {}", roleType);
            }
        }
    }

    private String getDescriptionFor(RoleType roleType) {
        return switch (roleType) {
            case ADMIN -> "Administrador con acceso completo al sistema";
            case EMPLOYEE -> "Empleado con acceso limitado a gestión de productos";
            case USER -> "Usuario básico del sistema";
        };
    }

    private void seedAdminUser() {
        String adminEmail = "admin@malva.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            User admin = new User();
            admin.setName("Administrador");
            admin.setLastName("Sistema");
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            admin.setSystemAdmin(true);
            admin.setRole(adminRole);

            userRepository.save(admin);
            log.info("Usuario administrador creado: {}", adminEmail);
        } else {
            log.info("Usuario administrador ya existe: {}", adminEmail);
        }
    }
}
