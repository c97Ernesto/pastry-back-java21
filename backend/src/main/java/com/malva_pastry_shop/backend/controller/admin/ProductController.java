package com.malva_pastry_shop.backend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.malva_pastry_shop.backend.domain.inventory.Product;
import com.malva_pastry_shop.backend.service.CategoryService;
import com.malva_pastry_shop.backend.service.ProductService;
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
}