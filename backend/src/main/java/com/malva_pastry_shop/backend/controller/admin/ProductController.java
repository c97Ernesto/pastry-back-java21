package com.malva_pastry_shop.backend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.malva_pastry_shop.backend.domain.inventory.Product;
import com.malva_pastry_shop.backend.dto.request.CreateProductRequest;
import com.malva_pastry_shop.backend.service.CategoryService;
import com.malva_pastry_shop.backend.service.ProductService;
import com.malva_pastry_shop.backend.domain.auth.User;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products;

        if (search != null && !search.isBlank()) {
            products = productService.search(search, pageable);
            model.addAttribute("search", search);
        } else if (categoryId != null) {
            products = productService.findByCategoryId(categoryId, pageable);
            model.addAttribute("categoryId", categoryId);
        } else {
            products = productService.findAllActive(pageable);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll(Pageable.unpaged()));
        model.addAttribute("pageTitle", "Productos");
        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new CreateProductRequest());
        model.addAttribute("categories", categoryService.findAll(Pageable.unpaged()));
        model.addAttribute("pageTitle", "Nuevo Producto");
        return "products/create";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("product") CreateProductRequest request,
            BindingResult result,
            @AuthenticationPrincipal User currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll(Pageable.unpaged()));
            model.addAttribute("pageTitle", "Nuevo Producto");
            return "products/create";
        }

        try {
            productService.create(request, currentUser);
            redirectAttributes.addFlashAttribute("success", "Producto creado exitosamente");
            return "redirect:/products";
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAll(Pageable.unpaged()));
            model.addAttribute("pageTitle", "Nuevo Producto");
            return "products/create";
        }
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        try {
            Product product = productService.findById(id);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getName());
            return "products/show";
        } catch (EntityNotFoundException e) {
            return "redirect:/products";
        }
    }
}