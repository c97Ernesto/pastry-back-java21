package com.malva_pastry_shop.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.malva_pastry_shop.backend.domain.inventory.Ingredient;
import com.malva_pastry_shop.backend.dto.request.IngredientRequest;
import com.malva_pastry_shop.backend.repository.IngredientRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    // ========== Consultas ==========

    public Page<Ingredient> findAll(Pageable pageable) {
        return ingredientRepository.findAll(pageable);
    }

    public Page<Ingredient> search(String name, Pageable pageable) {
        return ingredientRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Ingredient findById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente no encontrado con ID: " + id));
    }

    public List<Ingredient> findAllForSelect() {
        return ingredientRepository.findAllByOrderByNameAsc();
    }

    // ========== CRUD ==========

    @Transactional
    public Ingredient create(IngredientRequest request) {
        if (ingredientRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe un ingrediente con el nombre: " + request.getName());
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setUnitCost(request.getUnitCost());
        ingredient.setUnitOfMeasure(request.getUnitOfMeasure());

        return ingredientRepository.save(ingredient);
    }

}