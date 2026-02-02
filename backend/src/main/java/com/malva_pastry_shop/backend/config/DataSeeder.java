package com.malva_pastry_shop.backend.config;

import com.malva_pastry_shop.backend.domain.auth.Role;
import com.malva_pastry_shop.backend.domain.auth.RoleType;
import com.malva_pastry_shop.backend.domain.auth.User;
import com.malva_pastry_shop.backend.domain.storefront.Category;
import com.malva_pastry_shop.backend.domain.inventory.Ingredient;
import com.malva_pastry_shop.backend.domain.storefront.Product;
import com.malva_pastry_shop.backend.domain.inventory.UnitOfMeasure;
import com.malva_pastry_shop.backend.repository.CategoryRepository;
import com.malva_pastry_shop.backend.repository.IngredientRepository;
import com.malva_pastry_shop.backend.repository.ProductRepository;
import com.malva_pastry_shop.backend.repository.RoleRepository;
import com.malva_pastry_shop.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

        private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final CategoryRepository categoryRepository;
        private final ProductRepository productRepository;
        private final IngredientRepository ingredientRepository;
        private final PasswordEncoder passwordEncoder;

        public DataSeeder(RoleRepository roleRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository,
                        ProductRepository productRepository,
                        IngredientRepository ingredientRepository,
                        PasswordEncoder passwordEncoder) {
                this.roleRepository = roleRepository;
                this.userRepository = userRepository;
                this.categoryRepository = categoryRepository;
                this.productRepository = productRepository;
                this.ingredientRepository = ingredientRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                seedRoles();
                seedBasicUsers();
                seedIngredients();
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

        private void seedBasicUsers() {
                String sysAdminEmail = "sysadmin@malva.com";
                if (userRepository.findByEmail(sysAdminEmail).isEmpty()) {
                        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

                        User admin = new User();
                        admin.setName("Administrador");
                        admin.setLastName("Sistema");
                        admin.setEmail(sysAdminEmail);
                        admin.setPasswordHash(passwordEncoder.encode("sysadmin123"));
                        admin.setEnabled(true);
                        admin.setSystemAdmin(true);
                        admin.setRole(adminRole);

                        userRepository.save(admin);
                        log.info("Usuario administrador creado: {}", sysAdminEmail);
                } else {
                        log.info("Usuario administrador ya existe: {}", sysAdminEmail);
                }

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
                        admin.setSystemAdmin(false);
                        admin.setRole(adminRole);

                        userRepository.save(admin);
                        log.info("Usuario administrador creado: {}", adminEmail);
                } else {
                        log.info("Usuario administrador ya existe: {}", adminEmail);
                }

                String employeeEmail = "employee@malva.com";
                if (userRepository.findByEmail(employeeEmail).isEmpty()) {
                        Role employeeRole = roleRepository.findByName(RoleType.EMPLOYEE)
                                        .orElseThrow(() -> new RuntimeException("Rol EMPLOYEE no encontrado"));

                        User employee = new User();
                        employee.setName("Empleado");
                        employee.setLastName("Sistema");
                        employee.setEmail(employeeEmail);
                        employee.setPasswordHash(passwordEncoder.encode("employee123"));
                        employee.setEnabled(true);
                        employee.setSystemAdmin(false);
                        employee.setRole(employeeRole);

                        userRepository.save(employee);
                        log.info("Usuario empleado creado: {}", employeeEmail);
                } else {
                        log.info("Usuario empleado ya existe: {}", employeeEmail);
                }

                String userEmail = "user@malva.com";
                if (userRepository.findByEmail(userEmail).isEmpty()) {
                        Role userRole = roleRepository.findByName(RoleType.USER)
                                        .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

                        User user = new User();
                        user.setName("Usuario");
                        user.setLastName("Sistema");
                        user.setEmail(userEmail);
                        user.setPasswordHash(passwordEncoder.encode("user123"));
                        user.setEnabled(true);
                        user.setSystemAdmin(false);
                        user.setRole(userRole);

                        userRepository.save(user);
                        log.info("Usuario usuario creado: {}", userEmail);
                } else {
                        log.info("Usuario usuario ya existe: {}", userEmail);
                }
        }

        private void seedIngredients() {
                if (ingredientRepository.count() > 0) {
                        log.info("Ya existen ingredientes en la base de datos. Saltando seed de ingredientes.");
                        return;
                }

                int totalIngredients = 0;

                // ========== HARINAS Y BASES ==========
                createIngredient("Harina de Trigo", "Harina todo uso para repostería",
                                new BigDecimal("25.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Harina Integral", "Harina de trigo integral para productos saludables",
                                new BigDecimal("32.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Harina de Almendra", "Harina de almendra para productos sin gluten",
                                new BigDecimal("180.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Maicena", "Fécula de maíz para espesar y hornear",
                                new BigDecimal("35.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Harina de Avena", "Harina de avena para galletas saludables",
                                new BigDecimal("45.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Harina de Coco", "Harina de coco para recetas veganas",
                                new BigDecimal("95.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Polvo para Hornear", "Polvo de hornear (Royal o similar)",
                                new BigDecimal("85.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Bicarbonato de Sodio", "Bicarbonato para levado y reacciones químicas",
                                new BigDecimal("40.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 8;

                // ========== AZÚCARES Y ENDULZANTES ==========
                createIngredient("Azúcar Blanca", "Azúcar refinada blanca estándar",
                                new BigDecimal("22.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Azúcar Morena", "Azúcar morena o mascabado",
                                new BigDecimal("28.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Azúcar Glass", "Azúcar glass (azúcar pulverizada) para decoración",
                                new BigDecimal("30.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Miel de Abeja", "Miel natural de abeja pura",
                                new BigDecimal("120.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Jarabe de Maple", "Jarabe de arce natural",
                                new BigDecimal("280.00"), UnitOfMeasure.LITRO);
                createIngredient("Piloncillo", "Azúcar de caña sin refinar (panela)",
                                new BigDecimal("35.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Stevia", "Endulzante natural bajo en calorías",
                                new BigDecimal("450.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 7;

                // ========== LÁCTEOS ==========
                createIngredient("Leche Entera", "Leche de vaca entera pasteurizada",
                                new BigDecimal("18.00"), UnitOfMeasure.LITRO);
                createIngredient("Crema para Batir", "Crema de leche para batir (35% grasa)",
                                new BigDecimal("65.00"), UnitOfMeasure.LITRO);
                createIngredient("Mantequilla", "Mantequilla sin sal para repostería",
                                new BigDecimal("95.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Queso Crema", "Queso crema estilo Philadelphia",
                                new BigDecimal("85.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Leche Condensada", "Leche condensada azucarada",
                                new BigDecimal("45.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Leche Evaporada", "Leche evaporada sin azúcar",
                                new BigDecimal("32.00"), UnitOfMeasure.LITRO);
                createIngredient("Leche de Almendra", "Leche vegetal de almendra para productos veganos",
                                new BigDecimal("55.00"), UnitOfMeasure.LITRO);
                createIngredient("Yogurt Natural", "Yogurt natural sin azúcar",
                                new BigDecimal("38.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 8;

                // ========== GRASAS ==========
                createIngredient("Aceite Vegetal", "Aceite vegetal neutro para hornear",
                                new BigDecimal("35.00"), UnitOfMeasure.LITRO);
                createIngredient("Aceite de Oliva", "Aceite de oliva extra virgen",
                                new BigDecimal("95.00"), UnitOfMeasure.LITRO);
                createIngredient("Manteca Vegetal", "Manteca vegetal para repostería",
                                new BigDecimal("42.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Aceite de Coco", "Aceite de coco virgen para productos veganos",
                                new BigDecimal("120.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 4;

                // ========== HUEVOS Y PROTEÍNAS ==========
                createIngredient("Huevos", "Huevos frescos de gallina tamaño grande",
                                new BigDecimal("2.50"), UnitOfMeasure.UNIDAD);
                createIngredient("Clara de Huevo", "Clara de huevo pasteurizada líquida",
                                new BigDecimal("75.00"), UnitOfMeasure.LITRO);
                createIngredient("Yema de Huevo", "Yema de huevo pasteurizada",
                                new BigDecimal("95.00"), UnitOfMeasure.LITRO);
                totalIngredients += 3;

                // ========== CHOCOLATES ==========
                createIngredient("Chocolate Amargo", "Chocolate oscuro 70% cacao",
                                new BigDecimal("180.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Chocolate de Leche", "Chocolate con leche premium",
                                new BigDecimal("150.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Chocolate Blanco", "Chocolate blanco de cobertura",
                                new BigDecimal("165.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Cacao en Polvo", "Cocoa en polvo sin azúcar",
                                new BigDecimal("95.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Nutella", "Crema de avellanas con cacao",
                                new BigDecimal("85.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Chispas de Chocolate", "Chips de chocolate semi-amargo",
                                new BigDecimal("125.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 6;

                // ========== LEVADURAS Y AGENTES ==========
                createIngredient("Levadura Fresca", "Levadura fresca para panadería",
                                new BigDecimal("65.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Levadura Seca", "Levadura seca instantánea",
                                new BigDecimal("180.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Cremor Tártaro", "Ácido para estabilizar merengues",
                                new BigDecimal("320.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 3;

                // ========== FRUTAS ==========
                createIngredient("Fresas Frescas", "Fresas frescas de temporada",
                                new BigDecimal("55.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Manzanas", "Manzanas rojas o verdes para hornear",
                                new BigDecimal("35.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Limones", "Limones frescos amarillos",
                                new BigDecimal("28.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Naranjas", "Naranjas frescas para jugo y ralladura",
                                new BigDecimal("25.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Frambuesas", "Frambuesas frescas o congeladas",
                                new BigDecimal("95.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Moras", "Moras azules (blueberries)",
                                new BigDecimal("85.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Cerezas", "Cerezas frescas sin hueso",
                                new BigDecimal("120.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Plátanos", "Plátanos maduros",
                                new BigDecimal("22.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Piña", "Piña fresca natural",
                                new BigDecimal("30.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Duraznos", "Duraznos en almíbar o frescos",
                                new BigDecimal("45.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 10;

                // ========== FRUTOS SECOS Y SEMILLAS ==========
                createIngredient("Nueces", "Nueces pecanas o de castilla",
                                new BigDecimal("220.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Almendras", "Almendras naturales sin piel",
                                new BigDecimal("185.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Avellanas", "Avellanas tostadas",
                                new BigDecimal("210.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Coco Rallado", "Coco deshidratado rallado",
                                new BigDecimal("75.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Pasas", "Pasas de uva sin semilla",
                                new BigDecimal("65.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Pistaches", "Pistaches sin cáscara",
                                new BigDecimal("350.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Cacahuates", "Cacahuates tostados sin sal",
                                new BigDecimal("55.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Semillas de Amapola", "Semillas de amapola para decoración",
                                new BigDecimal("180.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 8;

                // ========== ESPECIAS Y SABORIZANTES ==========
                createIngredient("Extracto de Vainilla", "Extracto puro de vainilla",
                                new BigDecimal("420.00"), UnitOfMeasure.LITRO);
                createIngredient("Vainilla en Vaina", "Vainas de vainilla natural",
                                new BigDecimal("8.50"), UnitOfMeasure.UNIDAD);
                createIngredient("Canela en Polvo", "Canela molida de Ceilán",
                                new BigDecimal("95.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Canela en Rama", "Ramas de canela entera",
                                new BigDecimal("120.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Jengibre en Polvo", "Jengibre molido",
                                new BigDecimal("110.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Nuez Moscada", "Nuez moscada molida",
                                new BigDecimal("180.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Sal", "Sal fina de mesa",
                                new BigDecimal("8.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Sal de Mar", "Sal marina en escamas para decoración",
                                new BigDecimal("45.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Esencia de Almendra", "Esencia artificial de almendra",
                                new BigDecimal("95.00"), UnitOfMeasure.LITRO);
                createIngredient("Azahar", "Agua de azahar para pan de muerto",
                                new BigDecimal("85.00"), UnitOfMeasure.LITRO);
                totalIngredients += 10;

                // ========== OTROS INGREDIENTES ==========
                createIngredient("Gelatina Sin Sabor", "Grenetina en polvo o láminas",
                                new BigDecimal("180.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Café Instantáneo", "Café soluble para saborizante",
                                new BigDecimal("120.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Colorante Alimentario", "Colorantes vegetales variados",
                                new BigDecimal("15.00"), UnitOfMeasure.UNIDAD);
                createIngredient("Fondant", "Fondant para decoración de pasteles",
                                new BigDecimal("95.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Mermelada de Fresa", "Mermelada para rellenos",
                                new BigDecimal("55.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Caramelo", "Caramelo líquido o dulce de leche",
                                new BigDecimal("65.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Mazapán", "Mazapán para decoración",
                                new BigDecimal("85.00"), UnitOfMeasure.KILOGRAMO);
                createIngredient("Sprinkles", "Chispas de colores para decoración",
                                new BigDecimal("120.00"), UnitOfMeasure.KILOGRAMO);
                totalIngredients += 8;

                log.info("Seed de ingredientes completado. Total: {} ingredientes creados.", totalIngredients);
        }

        private void createIngredient(String name, String description, BigDecimal unitCost,
                        UnitOfMeasure unitOfMeasure) {
                if (ingredientRepository.findByNameIgnoreCase(name).isEmpty()) {
                        Ingredient ingredient = new Ingredient(name, unitCost, unitOfMeasure);
                        ingredient.setDescription(description);
                        ingredientRepository.save(ingredient);
                        log.debug("Ingrediente creado: {} - ${}/{}", name, unitCost, unitOfMeasure.getAbbreviation());
                } else {
                        log.debug("Ingrediente ya existe: {}", name);
                }
        }

        private Map<String, Category> seedCategories() {
                Map<String, Category> categoryMap = new HashMap<>();

                Object[][] categoriesData = {
                                { "Pasteles",
                                                "Pasteles tradicionales y personalizados para toda ocasión. Desde cumpleaños hasta bodas, elaborados con los mejores ingredientes." },
                                { "Cupcakes",
                                                "Deliciosos cupcakes artesanales con variedad de sabores y decoraciones únicas. Perfectos para eventos y antojos." },
                                { "Galletas",
                                                "Galletas horneadas diariamente con recetas tradicionales y creativas. Crujientes por fuera, suaves por dentro." },
                                { "Pan Dulce Mexicano",
                                                "Auténtico pan dulce mexicano: conchas, cuernos, orejas, polvorones y más. Tradición en cada bocado." },
                                { "Tartas y Pays",
                                                "Tartas frutales y pays caseros con masas crujientes y rellenos cremosos. Recetas de la abuela." },
                                { "Postres Individuales",
                                                "Postres gourmet en porciones individuales: mousse, tiramisú, cheesecake y más. Elegancia en cada cucharada." },
                                { "Panes Artesanales",
                                                "Panes especiales horneados con masa madre y técnicas artesanales. Baguettes, ciabattas y focaccias." },
                                { "Dulces y Confitería",
                                                "Dulces tradicionales mexicanos y confitería fina: mazapanes, cocadas, jamoncillos y trufas artesanales." },
                                { "Productos Veganos",
                                                "Opciones veganas sin sacrificar sabor. Pasteles, galletas y postres elaborados sin ingredientes de origen animal." },
                                { "Temporada y Festividades",
                                                "Productos especiales para Día de Muertos, Navidad, San Valentín y otras celebraciones. Edición limitada." }
                };

                for (Object[] data : categoriesData) {
                        String name = (String) data[0];
                        String description = (String) data[1];

                        var existingCategory = categoryRepository.findByNameIgnoreCase(name);
                        if (existingCategory.isEmpty()) {
                                Category category = new Category(name, description);
                                categoryRepository.save(category);
                                categoryMap.put(name, category);
                                log.info("Categoría creada: {}", name);
                        } else {
                                categoryMap.put(name, existingCategory.get());
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
                createProduct("Pastel de Chocolate Triple",
                                "Tres capas de bizcocho de chocolate con ganache, cubierto de chocolate belga", 3,
                                new BigDecimal("450.00"), categories.get("Pasteles"),
                                "/images/products/pastel-chocolate-triple.jpg");
                createProduct("Pastel Red Velvet", "Suave bizcocho rojo aterciopelado con frosting de queso crema", 2,
                                new BigDecimal("420.00"), categories.get("Pasteles"),
                                "/images/products/pastel-red-velvet.jpg");
                createProduct("Pastel de Vainilla Clásico", "Bizcocho esponjoso de vainilla con betún de mantequilla",
                                2,
                                new BigDecimal("380.00"), categories.get("Pasteles"),
                                "/images/products/pastel-vainilla-clasico.jpg");
                createProduct("Pastel de Zanahoria", "Con nueces, pasas y frosting de queso crema con canela", 2,
                                new BigDecimal("400.00"), categories.get("Pasteles"),
                                "/images/products/pastel-zanahoria.jpg");
                createProduct("Pastel de Fresas con Crema", "Bizcocho de vainilla con fresas frescas y crema chantilly",
                                1,
                                new BigDecimal("480.00"), categories.get("Pasteles"),
                                "/images/products/pastel-fresas-crema.jpg");
                createProduct("Pastel Tres Leches", "Bizcocho bañado en tres tipos de leche con merengue italiano", 1,
                                new BigDecimal("350.00"), categories.get("Pasteles"),
                                "/images/products/pastel-tres-leches.jpg");
                createProduct("Pastel Selva Negra", "Chocolate, cerezas y crema batida. Receta alemana tradicional", 3,
                                new BigDecimal("520.00"), categories.get("Pasteles"),
                                "/images/products/pastel-selva-negra.jpg");

                // Cupcakes (5 productos)
                createProduct("Cupcake de Chocolate", "Base de chocolate intenso con frosting de chocolate", 1,
                                new BigDecimal("45.00"), categories.get("Cupcakes"),
                                "/images/products/cupcake-chocolate.jpg");
                createProduct("Cupcake Red Velvet", "Cupcake rojo aterciopelado con queso crema", 1,
                                new BigDecimal("48.00"), categories.get("Cupcakes"),
                                "/images/products/cupcake-red-velvet.jpg");
                createProduct("Cupcake de Limón", "Sabor cítrico refrescante con frosting de limón", 1,
                                new BigDecimal("45.00"), categories.get("Cupcakes"),
                                "/images/products/cupcake-limon.jpg");
                createProduct("Cupcake de Nutella", "Relleno de Nutella con frosting de avellanas", 1,
                                new BigDecimal("55.00"), categories.get("Cupcakes"),
                                "/images/products/cupcake-nutella.jpg");
                createProduct("Cupcake de Café Moka", "Para los amantes del café con toque de chocolate", 1,
                                new BigDecimal("50.00"), categories.get("Cupcakes"),
                                "/images/products/cupcake-cafe-moka.jpg");

                // Galletas (5 productos)
                createProduct("Galletas de Chispas de Chocolate", "Clásicas con chips de chocolate semi-amargo. Docena",
                                1,
                                new BigDecimal("85.00"), categories.get("Galletas"),
                                "/images/products/galletas-chispas-chocolate.jpg");
                createProduct("Galletas de Avena con Pasas", "Saludables y deliciosas con pasas y canela. Docena", 1,
                                new BigDecimal("75.00"), categories.get("Galletas"),
                                "/images/products/galletas-avena-pasas.jpg");
                createProduct("Galletas de Mantequilla", "Tradicionales galletas danesas de mantequilla. Docena", 1,
                                new BigDecimal("70.00"), categories.get("Galletas"),
                                "/images/products/galletas-mantequilla.jpg");
                createProduct("Galletas de Jengibre", "Especiadas y crujientes, perfectas con café. Docena", 1,
                                new BigDecimal("80.00"), categories.get("Galletas"),
                                "/images/products/galletas-jengibre.jpg");
                createProduct("Galletas Snickerdoodle", "Con canela y azúcar, suaves por dentro. Docena", 1,
                                new BigDecimal("78.00"), categories.get("Galletas"),
                                "/images/products/galletas-snickerdoodle.jpg");

                // Pan Dulce Mexicano (6 productos)
                createProduct("Concha de Vainilla", "Pan dulce tradicional con costra de azúcar sabor vainilla", 0,
                                new BigDecimal("18.00"), categories.get("Pan Dulce Mexicano"),
                                "/images/products/concha-vainilla.jpg");
                createProduct("Concha de Chocolate", "Concha clásica con costra de chocolate", 0,
                                new BigDecimal("18.00"), categories.get("Pan Dulce Mexicano"),
                                "/images/products/concha-chocolate.jpg");
                createProduct("Cuerno de Mantequilla", "Esponjoso cuerno bañado en mantequilla y azúcar glass", 0,
                                new BigDecimal("20.00"), categories.get("Pan Dulce Mexicano"),
                                "/images/products/cuerno-mantequilla.jpg");
                createProduct("Oreja", "Pan hojaldrado caramelizado, crujiente y dulce", 0, new BigDecimal("22.00"),
                                categories.get("Pan Dulce Mexicano"),
                                "/images/products/oreja.jpg");
                createProduct("Polvorón Rosa", "Galleta de mantequilla que se deshace en la boca", 0,
                                new BigDecimal("15.00"), categories.get("Pan Dulce Mexicano"),
                                "/images/products/polvoron-rosa.jpg");
                createProduct("Garibaldi", "Panecillo cubierto de chochitos de colores", 0, new BigDecimal("16.00"),
                                categories.get("Pan Dulce Mexicano"),
                                "/images/products/garibaldi.jpg");

                // Tartas y Pays (5 productos)
                createProduct("Pay de Manzana", "Manzanas caramelizadas con canela en masa crujiente", 2,
                                new BigDecimal("280.00"), categories.get("Tartas y Pays"),
                                "/images/products/pay-manzana.jpg");
                createProduct("Pay de Limón", "Relleno cremoso de limón con merengue italiano", 2,
                                new BigDecimal("260.00"), categories.get("Tartas y Pays"),
                                "/images/products/pay-limon.jpg");
                createProduct("Pay de Nuez", "Nueces caramelizadas en base de mantequilla", 2, new BigDecimal("320.00"),
                                categories.get("Tartas y Pays"),
                                "/images/products/pay-nuez.jpg");
                createProduct("Tarta de Frutos Rojos", "Crema pastelera con frambuesas, moras y fresas", 1,
                                new BigDecimal("350.00"), categories.get("Tartas y Pays"),
                                "/images/products/tarta-frutos-rojos.jpg");
                createProduct("Tarta Tatin", "Tarta francesa invertida de manzana caramelizada", 2,
                                new BigDecimal("300.00"), categories.get("Tartas y Pays"),
                                "/images/products/tarta-tatin.jpg");

                // Postres Individuales (6 productos)
                createProduct("Tiramisú Individual", "Capas de mascarpone, café y bizcocho. Receta italiana", 1,
                                new BigDecimal("85.00"), categories.get("Postres Individuales"),
                                "/images/products/tiramisu-individual.jpg");
                createProduct("Mousse de Chocolate", "Mousse sedoso de chocolate belga 70% cacao", 1,
                                new BigDecimal("75.00"), categories.get("Postres Individuales"),
                                "/images/products/mousse-chocolate.jpg");
                createProduct("Cheesecake New York", "Cremoso cheesecake estilo americano con base de galleta", 2,
                                new BigDecimal("90.00"), categories.get("Postres Individuales"),
                                "/images/products/cheesecake-new-york.jpg");
                createProduct("Panna Cotta", "Postre italiano de nata con coulis de frutos rojos", 1,
                                new BigDecimal("70.00"), categories.get("Postres Individuales"),
                                "/images/products/panna-cotta.jpg");
                createProduct("Crème Brûlée", "Crema francesa con costra de caramelo crujiente", 1,
                                new BigDecimal("80.00"), categories.get("Postres Individuales"),
                                "/images/products/creme-brulee.jpg");
                createProduct("Profiteroles", "Choux rellenos de crema con chocolate caliente", 1,
                                new BigDecimal("95.00"), categories.get("Postres Individuales"),
                                "/images/products/profiteroles.jpg");

                // Panes Artesanales (4 productos)
                createProduct("Baguette Francesa", "Pan crujiente de masa madre fermentada 24 horas", 1,
                                new BigDecimal("45.00"), categories.get("Panes Artesanales"),
                                "/images/products/baguette-francesa.jpg");
                createProduct("Ciabatta Italiana", "Pan rústico italiano con corteza crujiente y miga aireada", 1,
                                new BigDecimal("50.00"), categories.get("Panes Artesanales"),
                                "/images/products/ciabatta-italiana.jpg");
                createProduct("Focaccia de Romero", "Pan plano italiano con aceite de oliva y romero fresco", 1,
                                new BigDecimal("65.00"), categories.get("Panes Artesanales"),
                                "/images/products/focaccia-romero.jpg");
                createProduct("Pan de Masa Madre", "Hogaza artesanal fermentada naturalmente 48 horas", 2,
                                new BigDecimal("85.00"), categories.get("Panes Artesanales"),
                                "/images/products/pan-masa-madre.jpg");

                // Dulces y Confitería (5 productos)
                createProduct("Mazapán de Almendra", "Dulce tradicional de almendra molida. Caja 12 piezas", 1,
                                new BigDecimal("120.00"), categories.get("Dulces y Confitería"),
                                "/images/products/mazapan-almendra.jpg");
                createProduct("Cocadas", "Dulce de coco rallado caramelizado. Bolsa 250g", 1, new BigDecimal("65.00"),
                                categories.get("Dulces y Confitería"),
                                "/images/products/cocadas.jpg");
                createProduct("Jamoncillo de Leche", "Dulce de leche tradicional mexicano. Caja 8 piezas", 1,
                                new BigDecimal("80.00"), categories.get("Dulces y Confitería"),
                                "/images/products/jamoncillo-leche.jpg");
                createProduct("Trufas de Chocolate", "Trufas artesanales de chocolate belga. Caja 6 piezas", 1,
                                new BigDecimal("150.00"), categories.get("Dulces y Confitería"),
                                "/images/products/trufas-chocolate.jpg");
                createProduct("Ate con Queso", "Dulce de membrillo artesanal con queso manchego. 300g", 1,
                                new BigDecimal("95.00"), categories.get("Dulces y Confitería"),
                                "/images/products/ate-queso.jpg");

                // Productos Veganos (4 productos)
                createProduct("Pastel Vegano de Chocolate", "Sin huevo ni lácteos, igual de delicioso", 2,
                                new BigDecimal("420.00"), categories.get("Productos Veganos"),
                                "/images/products/pastel-vegano-chocolate.jpg");
                createProduct("Cupcakes Veganos Variados", "Set de 4 cupcakes veganos de sabores surtidos", 1,
                                new BigDecimal("180.00"), categories.get("Productos Veganos"),
                                "/images/products/cupcakes-veganos.jpg");
                createProduct("Galletas Veganas de Avena", "Sin huevo, con chips de chocolate vegano. Docena", 1,
                                new BigDecimal("95.00"), categories.get("Productos Veganos"),
                                "/images/products/galletas-veganas-avena.jpg");
                createProduct("Brownie Vegano", "Brownie húmedo sin ingredientes animales", 1, new BigDecimal("55.00"),
                                categories.get("Productos Veganos"),
                                "/images/products/brownie-vegano.jpg");

                // Temporada y Festividades (3 productos)
                createProduct("Pan de Muerto", "Tradicional pan de temporada con azahar. Disponible Oct-Nov", 1,
                                new BigDecimal("65.00"), categories.get("Temporada y Festividades"),
                                "/images/products/pan-de-muerto.jpg");
                createProduct("Rosca de Reyes", "Con frutas cristalizadas y muñequito. Disponible en Enero", 2,
                                new BigDecimal("280.00"), categories.get("Temporada y Festividades"),
                                "/images/products/rosca-de-reyes.jpg");
                createProduct("Tronco de Navidad", "Bûche de Noël con chocolate y crema de castañas", 3,
                                new BigDecimal("450.00"), categories.get("Temporada y Festividades"),
                                "/images/products/tronco-navidad.jpg");

                log.info("Seed de productos completado. Total: 50 productos creados.");
        }

        private void createProduct(String name, String description, Integer preparationDays, BigDecimal basePrice,
                        Category category, String imageUrl) {
                Product product = new Product(name, basePrice);
                product.setDescription(description);
                product.setPreparationDays(preparationDays);
                product.setCategory(category);
                product.setImageUrl(imageUrl);
                productRepository.save(product);
                log.debug("Producto creado: {} - ${}", name, basePrice);
        }
}
