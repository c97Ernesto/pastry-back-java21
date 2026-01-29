package com.malva_pastry_shop.backend.controller.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.malva_pastry_shop.backend.domain.auth.User;
import com.malva_pastry_shop.backend.service.UserService;
import com.malva_pastry_shop.backend.service.inventory.IngredientService;
import com.malva_pastry_shop.backend.service.sales.SaleService;
import com.malva_pastry_shop.backend.service.storefront.CategoryService;
import com.malva_pastry_shop.backend.service.storefront.ProductService;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final IngredientService ingredientService;
    private final SaleService saleService;
    private final UserService userService;

    public DashboardController(ProductService productService,
                               CategoryService categoryService,
                               IngredientService ingredientService,
                               SaleService saleService,
                               UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.ingredientService = ingredientService;
        this.saleService = saleService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Dashboard");

        // Contadores de entidades (reutilizando servicios existentes)
        model.addAttribute("totalProductos", productService.findAllActive(Pageable.unpaged()).getTotalElements());
        model.addAttribute("totalCategorias", categoryService.findAllActive(Pageable.unpaged()).getTotalElements());
        model.addAttribute("totalIngredientes", ingredientService.findAllActive(Pageable.unpaged()).getTotalElements());
        model.addAttribute("totalUsuarios", userService.findAll(Pageable.unpaged()).getTotalElements());
        model.addAttribute("totalVentas", saleService.findAll(Pageable.unpaged()).getTotalElements());

        // Ventas e ingresos del dia
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        model.addAttribute("ventasHoy", saleService.countSalesInRange(startOfDay, endOfDay));
        model.addAttribute("ingresoHoy", saleService.totalRevenueInRange(startOfDay, endOfDay));

        // Ventas e ingresos del mes
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        model.addAttribute("ventasMes", saleService.countSalesInRange(startOfMonth, endOfDay));
        model.addAttribute("ingresoMes", saleService.totalRevenueInRange(startOfMonth, endOfDay));

        return "dashboard/index";
    }
}
