package com.malva_pastry_shop.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.malva_pastry_shop.backend.domain.auth.User;
import com.malva_pastry_shop.backend.domain.inventory.Category;
import com.malva_pastry_shop.backend.dto.request.CategoryRequest;
import com.malva_pastry_shop.backend.repository.CategoryRepository;
import com.malva_pastry_shop.backend.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // ========== Consultas ==========

    public Page<Category> findAllActive(Pageable pageable) {
        return categoryRepository.findByDeletedAtIsNull(pageable);
    }

    public Page<Category> search(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(name, pageable);
    }

    public Category findById(Long id) {
        return categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));
    }

    public Page<Category> findDeleted(Pageable pageable) {
        return categoryRepository.findByDeletedAtIsNotNull(pageable);
    }

    // ========== CRUD ==========

    @Transactional
    public Category create(CategoryRequest request) {
        if (categoryRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría activa con el nombre: " + request.getName());
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, CategoryRequest request) {
        Category category = findById(id);

        if (categoryRepository.existsByNameAndIdNotAndDeletedAtIsNull(request.getName(), id)) {
            throw new IllegalArgumentException("Ya existe otra categoría activa con el nombre: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return categoryRepository.save(category);
    }

    // ========== Soft Delete ==========

    @Transactional
    public void softDelete(Long id, User deletedBy) {
        Category category = findById(id);

        long productCount = productRepository.countByCategoryIdAndDeletedAtIsNull(id);
        if (productCount > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar la categoría porque tiene " + productCount + " producto(s) activo(s) asociado(s)");
        }

        category.softDelete(deletedBy);
        categoryRepository.save(category);
    }

    @Transactional
    public Category restore(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        if (category.getDeletedAt() == null) {
            throw new IllegalStateException("La categoría no está eliminada");
        }

        // Verificar que no exista otra categoría activa con el mismo nombre
        if (categoryRepository.existsByNameAndDeletedAtIsNull(category.getName())) {
            throw new IllegalStateException("Ya existe una categoría activa con el nombre: " + category.getName());
        }

        category.restore();
        return categoryRepository.save(category);
    }

    // ========== Hard Delete ==========

    @Transactional
    public void hardDelete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        // Solo se puede hacer hard delete si está en papelera
        if (category.getDeletedAt() == null) {
            throw new IllegalStateException("Solo se pueden eliminar permanentemente las categorías que están en la papelera");
        }

        // Verificar que no tenga productos (ni activos ni eliminados)
        long totalProducts = productRepository.countByCategoryId(id);
        if (totalProducts > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar permanentemente la categoría porque tiene " + totalProducts + " producto(s) asociado(s)");
        }

        categoryRepository.delete(category);
    }

    // ========== Utilidades ==========

    public long countProducts(Long categoryId) {
        return productRepository.countByCategoryIdAndDeletedAtIsNull(categoryId);
    }
}
