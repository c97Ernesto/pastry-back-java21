package com.malva_pastry_shop.backend.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.malva_pastry_shop.backend.domain.inventory.Ingredient;
import com.malva_pastry_shop.backend.domain.inventory.UnitOfMeasure;
import com.malva_pastry_shop.backend.dto.request.IngredientRequest;
import com.malva_pastry_shop.backend.service.IngredientService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    // ========== Listados ==========

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Ingredient> ingredients;

        if (search != null && !search.isBlank()) {
            ingredients = ingredientService.search(search, pageable);
            model.addAttribute("search", search);
        } else {
            ingredients = ingredientService.findAll(pageable);
        }

        model.addAttribute("ingredients", ingredients);
        model.addAttribute("pageTitle", "Ingredientes");
        return "ingredients/list";
    }

    // ========== CRUD ==========

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("ingredient", new IngredientRequest());
        model.addAttribute("unitsOfMeasure", UnitOfMeasure.values());
        model.addAttribute("pageTitle", "Nuevo Ingrediente");
        return "ingredients/create";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("ingredient") IngredientRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("unitsOfMeasure", UnitOfMeasure.values());
            model.addAttribute("pageTitle", "Nuevo Ingrediente");
            return "ingredients/create";
        }

        try {
            ingredientService.create(request);
            redirectAttributes.addFlashAttribute("success", "Ingrediente creado exitosamente");
            return "redirect:/ingredients";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("unitsOfMeasure", UnitOfMeasure.values());
            model.addAttribute("pageTitle", "Nuevo Ingrediente");
            return "ingredients/create";
        }
    }
}