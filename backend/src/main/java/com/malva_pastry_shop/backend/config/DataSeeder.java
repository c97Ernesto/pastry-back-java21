package com.malva_pastry_shop.backend.config;

import com.malva_pastry_shop.backend.domain.auth.Role;
import com.malva_pastry_shop.backend.domain.auth.RoleType;
import com.malva_pastry_shop.backend.domain.auth.User;
import com.malva_pastry_shop.backend.domain.inventory.Category;
import com.malva_pastry_shop.backend.domain.inventory.Product;
import com.malva_pastry_shop.backend.repository.CategoryRepository;
import com.malva_pastry_shop.backend.repository.ProductRepository;
import com.malva_pastry_shop.backend.repository.RoleRepository;
import com.malva_pastry_shop.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      ProductRepository productRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedRoles();
        seedAdminUser();
        Map<String, Category> categories = seedCategories();
        seedProducts(categories);
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

    private Map<String, Category> seedCategories() {
        Map<String, Category> categoryMap = new HashMap<>();

        Object[][] categoriesData = {
            {"Pasteles", "Pasteles tradicionales y personalizados para toda ocasión. Desde cumpleaños hasta bodas, elaborados con los mejores ingredientes."},
            {"Cupcakes", "Deliciosos cupcakes artesanales con variedad de sabores y decoraciones únicas. Perfectos para eventos y antojos."},
            {"Galletas", "Galletas horneadas diariamente con recetas tradicionales y creativas. Crujientes por fuera, suaves por dentro."},
            {"Pan Dulce Mexicano", "Auténtico pan dulce mexicano: conchas, cuernos, orejas, polvorones y más. Tradición en cada bocado."},
            {"Tartas y Pays", "Tartas frutales y pays caseros con masas crujientes y rellenos cremosos. Recetas de la abuela."},
            {"Postres Individuales", "Postres gourmet en porciones individuales: mousse, tiramisú, cheesecake y más. Elegancia en cada cucharada."},
            {"Panes Artesanales", "Panes especiales horneados con masa madre y técnicas artesanales. Baguettes, ciabattas y focaccias."},
            {"Dulces y Confitería", "Dulces tradicionales mexicanos y confitería fina: mazapanes, cocadas, jamoncillos y trufas artesanales."},
            {"Productos Veganos", "Opciones veganas sin sacrificar sabor. Pasteles, galletas y postres elaborados sin ingredientes de origen animal."},
            {"Temporada y Festividades", "Productos especiales para Día de Muertos, Navidad, San Valentín y otras celebraciones. Edición limitada."}
        };

        for (Object[] data : categoriesData) {
            String name = (String) data[0];
            String description = (String) data[1];

            if (!categoryRepository.existsByName(name)) {
                Category category = new Category(name, description);
                categoryRepository.save(category);
                categoryMap.put(name, category);
                log.info("Categoría creada: {}", name);
            } else {
                categoryRepository.findByName(name).ifPresent(c -> categoryMap.put(name, c));
                log.info("Categoría ya existe: {}", name);
            }
        }

        log.info("Total de categorías procesadas: {}", categoryMap.size());
        return categoryMap;
    }

    private void seedProducts(Map<String, Category> categories) {
        if (productRepository.count() > 0) {
            log.info("Ya existen productos en la base de datos. Saltando seed de productos.");
            return;
        }

        // Pasteles (7 productos)
        createProduct("Pastel de Chocolate Triple", "Tres capas de bizcocho de chocolate con ganache, cubierto de chocolate belga", 3, new BigDecimal("450.00"), categories.get("Pasteles"));
        createProduct("Pastel Red Velvet", "Suave bizcocho rojo aterciopelado con frosting de queso crema", 2, new BigDecimal("420.00"), categories.get("Pasteles"));
        createProduct("Pastel de Vainilla Clásico", "Bizcocho esponjoso de vainilla con betún de mantequilla", 2, new BigDecimal("380.00"), categories.get("Pasteles"));
        createProduct("Pastel de Zanahoria", "Con nueces, pasas y frosting de queso crema con canela", 2, new BigDecimal("400.00"), categories.get("Pasteles"));
        createProduct("Pastel de Fresas con Crema", "Bizcocho de vainilla con fresas frescas y crema chantilly", 1, new BigDecimal("480.00"), categories.get("Pasteles"));
        createProduct("Pastel Tres Leches", "Bizcocho bañado en tres tipos de leche con merengue italiano", 1, new BigDecimal("350.00"), categories.get("Pasteles"));
        createProduct("Pastel Selva Negra", "Chocolate, cerezas y crema batida. Receta alemana tradicional", 3, new BigDecimal("520.00"), categories.get("Pasteles"));

        // Cupcakes (5 productos)
        createProduct("Cupcake de Chocolate", "Base de chocolate intenso con frosting de chocolate", 1, new BigDecimal("45.00"), categories.get("Cupcakes"));
        createProduct("Cupcake Red Velvet", "Cupcake rojo aterciopelado con queso crema", 1, new BigDecimal("48.00"), categories.get("Cupcakes"));
        createProduct("Cupcake de Limón", "Sabor cítrico refrescante con frosting de limón", 1, new BigDecimal("45.00"), categories.get("Cupcakes"));
        createProduct("Cupcake de Nutella", "Relleno de Nutella con frosting de avellanas", 1, new BigDecimal("55.00"), categories.get("Cupcakes"));
        createProduct("Cupcake de Café Moka", "Para los amantes del café con toque de chocolate", 1, new BigDecimal("50.00"), categories.get("Cupcakes"));

        // Galletas (5 productos)
        createProduct("Galletas de Chispas de Chocolate", "Clásicas con chips de chocolate semi-amargo. Docena", 1, new BigDecimal("85.00"), categories.get("Galletas"));
        createProduct("Galletas de Avena con Pasas", "Saludables y deliciosas con pasas y canela. Docena", 1, new BigDecimal("75.00"), categories.get("Galletas"));
        createProduct("Galletas de Mantequilla", "Tradicionales galletas danesas de mantequilla. Docena", 1, new BigDecimal("70.00"), categories.get("Galletas"));
        createProduct("Galletas de Jengibre", "Especiadas y crujientes, perfectas con café. Docena", 1, new BigDecimal("80.00"), categories.get("Galletas"));
        createProduct("Galletas Snickerdoodle", "Con canela y azúcar, suaves por dentro. Docena", 1, new BigDecimal("78.00"), categories.get("Galletas"));

        // Pan Dulce Mexicano (6 productos)
        createProduct("Concha de Vainilla", "Pan dulce tradicional con costra de azúcar sabor vainilla", 0, new BigDecimal("18.00"), categories.get("Pan Dulce Mexicano"));
        createProduct("Concha de Chocolate", "Concha clásica con costra de chocolate", 0, new BigDecimal("18.00"), categories.get("Pan Dulce Mexicano"));
        createProduct("Cuerno de Mantequilla", "Esponjoso cuerno bañado en mantequilla y azúcar glass", 0, new BigDecimal("20.00"), categories.get("Pan Dulce Mexicano"));
        createProduct("Oreja", "Pan hojaldrado caramelizado, crujiente y dulce", 0, new BigDecimal("22.00"), categories.get("Pan Dulce Mexicano"));
        createProduct("Polvorón Rosa", "Galleta de mantequilla que se deshace en la boca", 0, new BigDecimal("15.00"), categories.get("Pan Dulce Mexicano"));
        createProduct("Garibaldi", "Panecillo cubierto de chochitos de colores", 0, new BigDecimal("16.00"), categories.get("Pan Dulce Mexicano"));

        // Tartas y Pays (5 productos)
        createProduct("Pay de Manzana", "Manzanas caramelizadas con canela en masa crujiente", 2, new BigDecimal("280.00"), categories.get("Tartas y Pays"));
        createProduct("Pay de Limón", "Relleno cremoso de limón con merengue italiano", 2, new BigDecimal("260.00"), categories.get("Tartas y Pays"));
        createProduct("Pay de Nuez", "Nueces caramelizadas en base de mantequilla", 2, new BigDecimal("320.00"), categories.get("Tartas y Pays"));
        createProduct("Tarta de Frutos Rojos", "Crema pastelera con frambuesas, moras y fresas", 1, new BigDecimal("350.00"), categories.get("Tartas y Pays"));
        createProduct("Tarta Tatin", "Tarta francesa invertida de manzana caramelizada", 2, new BigDecimal("300.00"), categories.get("Tartas y Pays"));

        // Postres Individuales (6 productos)
        createProduct("Tiramisú Individual", "Capas de mascarpone, café y bizcocho. Receta italiana", 1, new BigDecimal("85.00"), categories.get("Postres Individuales"));
        createProduct("Mousse de Chocolate", "Mousse sedoso de chocolate belga 70% cacao", 1, new BigDecimal("75.00"), categories.get("Postres Individuales"));
        createProduct("Cheesecake New York", "Cremoso cheesecake estilo americano con base de galleta", 2, new BigDecimal("90.00"), categories.get("Postres Individuales"));
        createProduct("Panna Cotta", "Postre italiano de nata con coulis de frutos rojos", 1, new BigDecimal("70.00"), categories.get("Postres Individuales"));
        createProduct("Crème Brûlée", "Crema francesa con costra de caramelo crujiente", 1, new BigDecimal("80.00"), categories.get("Postres Individuales"));
        createProduct("Profiteroles", "Choux rellenos de crema con chocolate caliente", 1, new BigDecimal("95.00"), categories.get("Postres Individuales"));

        // Panes Artesanales (4 productos)
        createProduct("Baguette Francesa", "Pan crujiente de masa madre fermentada 24 horas", 1, new BigDecimal("45.00"), categories.get("Panes Artesanales"));
        createProduct("Ciabatta Italiana", "Pan rústico italiano con corteza crujiente y miga aireada", 1, new BigDecimal("50.00"), categories.get("Panes Artesanales"));
        createProduct("Focaccia de Romero", "Pan plano italiano con aceite de oliva y romero fresco", 1, new BigDecimal("65.00"), categories.get("Panes Artesanales"));
        createProduct("Pan de Masa Madre", "Hogaza artesanal fermentada naturalmente 48 horas", 2, new BigDecimal("85.00"), categories.get("Panes Artesanales"));

        // Dulces y Confitería (5 productos)
        createProduct("Mazapán de Almendra", "Dulce tradicional de almendra molida. Caja 12 piezas", 1, new BigDecimal("120.00"), categories.get("Dulces y Confitería"));
        createProduct("Cocadas", "Dulce de coco rallado caramelizado. Bolsa 250g", 1, new BigDecimal("65.00"), categories.get("Dulces y Confitería"));
        createProduct("Jamoncillo de Leche", "Dulce de leche tradicional mexicano. Caja 8 piezas", 1, new BigDecimal("80.00"), categories.get("Dulces y Confitería"));
        createProduct("Trufas de Chocolate", "Trufas artesanales de chocolate belga. Caja 6 piezas", 1, new BigDecimal("150.00"), categories.get("Dulces y Confitería"));
        createProduct("Ate con Queso", "Dulce de membrillo artesanal con queso manchego. 300g", 1, new BigDecimal("95.00"), categories.get("Dulces y Confitería"));

        // Productos Veganos (4 productos)
        createProduct("Pastel Vegano de Chocolate", "Sin huevo ni lácteos, igual de delicioso", 2, new BigDecimal("420.00"), categories.get("Productos Veganos"));
        createProduct("Cupcakes Veganos Variados", "Set de 4 cupcakes veganos de sabores surtidos", 1, new BigDecimal("180.00"), categories.get("Productos Veganos"));
        createProduct("Galletas Veganas de Avena", "Sin huevo, con chips de chocolate vegano. Docena", 1, new BigDecimal("95.00"), categories.get("Productos Veganos"));
        createProduct("Brownie Vegano", "Brownie húmedo sin ingredientes animales", 1, new BigDecimal("55.00"), categories.get("Productos Veganos"));

        // Temporada y Festividades (3 productos)
        createProduct("Pan de Muerto", "Tradicional pan de temporada con azahar. Disponible Oct-Nov", 1, new BigDecimal("65.00"), categories.get("Temporada y Festividades"));
        createProduct("Rosca de Reyes", "Con frutas cristalizadas y muñequito. Disponible en Enero", 2, new BigDecimal("280.00"), categories.get("Temporada y Festividades"));
        createProduct("Tronco de Navidad", "Bûche de Noël con chocolate y crema de castañas", 3, new BigDecimal("450.00"), categories.get("Temporada y Festividades"));

        log.info("Seed de productos completado. Total: 50 productos creados.");
    }

    private void createProduct(String name, String description, Integer preparationDays, BigDecimal basePrice, Category category) {
        Product product = new Product(name, basePrice);
        product.setDescription(description);
        product.setPreparationDays(preparationDays);
        product.setCategory(category);
        productRepository.save(product);
        log.debug("Producto creado: {} - ${}", name, basePrice);
    }
}
