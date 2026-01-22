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

import com.malva_pastry_shop.backend.domain.storefront.Tag;
import com.malva_pastry_shop.backend.dto.request.TagRequest;
import com.malva_pastry_shop.backend.service.storefront.TagService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // ========== Listados ==========

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Tag> tags;

        if (search != null && !search.isBlank()) {
            tags = tagService.search(search, pageable);
            model.addAttribute("search", search);
        } else {
            tags = tagService.findAllActive(pageable);
        }

        model.addAttribute("tags", tags);
        model.addAttribute("pageTitle", "Tags");
        return "tags/list";
    }

    // ========== CRUD ==========

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tag", new TagRequest());
        model.addAttribute("pageTitle", "Nuevo Tag");
        return "tags/create";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("tag") TagRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Nuevo Tag");
            return "tags/create";
        }

        try {
            tagService.create(request);
            redirectAttributes.addFlashAttribute("success", "Tag creado exitosamente");
            return "redirect:/tags";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", "Nuevo Tag");
            return "tags/create";
        }
    }
}
