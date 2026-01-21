package com.malva_pastry_shop.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.malva_pastry_shop.backend.domain.auth.User;
import com.malva_pastry_shop.backend.domain.inventory.Ingredient;
import com.malva_pastry_shop.backend.dto.request.IngredientRequest;
import com.malva_pastry_shop.backend.repository.IngredientRepository;
import com.malva_pastry_shop.backend.repository.ProductIngredientRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ProductIngredientRepository productIngredientRepository;

    public IngredientService(IngredientRepository ingredientRepository,
            ProductIngredientRepository productIngredientRepository) {
        this.ingredientRepository = ingredientRepository;
        this.productIngredientRepository = productIngredientRepository;
    }

    // ========== Consultas ==========

    public Page<Ingredient> findAllActive(Pageable pageable) {
        return ingredientRepository.findByDeletedAtIsNull(pageable);
    }

    public Page<Ingredient> search(String name, Pageable pageable) {
        return ingredientRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(name, pageable);
    }

    public Ingredient findById(Long id) {
        return ingredientRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente no encontrado con ID: " + id));
    }

    public List<Ingredient> findAllForSelect() {
        return ingredientRepository.findByDeletedAtIsNullOrderByNameAsc();
    }

    public Page<Ingredient> findDeleted(Pageable pageable) {
        return ingredientRepository.findByDeletedAtIsNotNull(pageable);
    }

    // ========== CRUD ==========

    @Transactional
    public Ingredient create(IngredientRequest request) {
        if (ingredientRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new IllegalArgumentException("Ya existe un ingrediente activo con el nombre: " + request.getName());
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setUnitCost(request.getUnitCost());
        ingredient.setUnitOfMeasure(request.getUnitOfMeasure());

        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public Ingredient update(Long id, IngredientRequest request) {
        Ingredient ingredient = findById(id);

        if (ingredientRepository.existsByNameAndIdNotAndDeletedAtIsNull(request.getName(), id)) {
            throw new IllegalArgumentException("Ya existe otro ingrediente activo con el nombre: " + request.getName());
        }

        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setUnitCost(request.getUnitCost());
        ingredient.setUnitOfMeasure(request.getUnitOfMeasure());

        return ingredientRepository.save(ingredient);
    }

    // ========== Soft Delete ==========

    @Transactional
    public void softDelete(Long id, User deletedBy) {
        Ingredient ingredient = findById(id);

        // Verificar que no este en uso por productos activos
        long usageCount = productIngredientRepository.countByIngredientId(id);
        if (usageCount > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar el ingrediente porque esta siendo usado en " + usageCount + " producto(s)");
        }

        ingredient.softDelete(deletedBy);
        ingredientRepository.save(ingredient);
    }

    @Transactional
    public Ingredient restore(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente no encontrado"));

        if (ingredient.getDeletedAt() == null) {
            throw new IllegalStateException("El ingrediente no esta eliminado");
        }

        // Verificar que no exista otro ingrediente activo con el mismo nombre
        if (ingredientRepository.existsByNameAndDeletedAtIsNull(ingredient.getName())) {
            throw new IllegalStateException("Ya existe un ingrediente activo con el nombre: " + ingredient.getName());
        }

        ingredient.restore();
        return ingredientRepository.save(ingredient);
    }

    // ========== Hard Delete ==========

    @Transactional
    public void hardDelete(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente no encontrado"));

        // Solo se puede hacer hard delete si esta en papelera
        if (ingredient.getDeletedAt() == null) {
            throw new IllegalStateException(
                    "Solo se pueden eliminar permanentemente los ingredientes que estan en la papelera");
        }

        // Verificar que no este en uso (aunque este eliminado)
        long usageCount = productIngredientRepository.countByIngredientId(id);
        if (usageCount > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar permanentemente el ingrediente porque esta asociado a " + usageCount + " producto(s)");
        }

        ingredientRepository.delete(ingredient);
    }

    // ========== Utilidades ==========

    public long countProductsUsingIngredient(Long ingredientId) {
        return productIngredientRepository.countByIngredientId(ingredientId);
    }
}
