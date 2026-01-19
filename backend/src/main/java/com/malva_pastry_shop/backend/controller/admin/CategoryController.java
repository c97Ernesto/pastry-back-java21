package com.malva_pastry_shop.backend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.malva_pastry_shop.backend.domain.inventory.Category;
import com.malva_pastry_shop.backend.dto.request.CreateCategoryRequest;
import com.malva_pastry_shop.backend.service.CategoryService;
import com.malva_pastry_shop.backend.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Category> categories;

        if (search != null && !search.isBlank()) {
            categories = categoryService.search(search, pageable);
            model.addAttribute("search", search);
        } else {
            categories = categoryService.findAll(pageable);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Categorias");
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CreateCategoryRequest());
        model.addAttribute("pageTitle", "Nueva Categoria");
        return "categories/create";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("category") CreateCategoryRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Nueva Categoria");
            return "categories/create";
        }

        try {
            categoryService.create(request);
            redirectAttributes.addFlashAttribute("success", "Categoria creada exitosamente");
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", "Nueva Categoria");
            return "categories/create";
        }
    }

}
