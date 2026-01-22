package com.malva_pastry_shop.backend.service;

import com.malva_pastry_shop.backend.domain.inventory.Product;
import com.malva_pastry_shop.backend.domain.inventory.Category;
import com.malva_pastry_shop.backend.domain.auth.User;
import com.malva_pastry_shop.backend.dto.request.ProductRequest;
import com.malva_pastry_shop.backend.repository.CategoryRepository;
import com.malva_pastry_shop.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // ========== Consultas ==========

    public Page<Product> findAllActive(Pageable pageable) {
        return productRepository.findByDeletedAtIsNull(pageable);
    }

    public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndDeletedAtIsNull(categoryId, pageable);
    }

    public Page<Product> search(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(name, pageable);
    }

    public Product findById(Long id) {
        return productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
    }

    public Page<Product> findDeleted(Pageable pageable) {
        return productRepository.findByDeletedAtIsNotNull(pageable);
    }

    // ========== CRUD ==========

    @Transactional
    public Product create(ProductRequest request, User createdBy) {
        validateProductName(request.getName(), null);

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPreparationDays(request.getPreparationDays());
        product.setBasePrice(request.getBasePrice());
        product.setCreatedBy(createdBy);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        validateProductName(request.getName(), id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPreparationDays(request.getPreparationDays());
        product.setBasePrice(request.getBasePrice());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        return productRepository.save(product);
    }

    // ========== Soft Delete ==========

    @Transactional
    public void softDelete(Long id, User deletedBy) {
        Product product = findById(id);
        product.softDelete(deletedBy);
        productRepository.save(product);
    }

    @Transactional
    public Product restore(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        if (product.getDeletedAt() == null) {
            throw new IllegalStateException("El producto no está eliminado");
        }

        // Verificar que no exista otro producto activo con el mismo nombre (case-insensitive)
        productRepository.findByNameIgnoreCase(product.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id) && !existing.isDeleted()) {
                throw new IllegalStateException("Ya existe un producto activo con el nombre: " + product.getName());
            }
        });

        product.restore();
        return productRepository.save(product);
    }

    // ========== Hard Delete ==========

    @Transactional
    public void hardDelete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // Solo se puede hacer hard delete si está en papelera
        if (product.getDeletedAt() == null) {
            throw new IllegalStateException("Solo se pueden eliminar permanentemente los productos que están en la papelera");
        }

        productRepository.delete(product);
    }

    // ========== Validaciones ==========

    /**
     * Valida que el nombre del producto sea único (case-insensitive).
     * Verifica tanto productos activos como en papelera.
     *
     * @param name      Nombre a validar
     * @param excludeId ID del producto a excluir (para updates), null para creates
     */
    private void validateProductName(String name, Long excludeId) {
        productRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
            // Si es update y es el mismo producto, no hay conflicto
            if (excludeId != null && existing.getId().equals(excludeId)) {
                return;
            }

            if (existing.isDeleted()) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con el nombre '" + name + "' en la papelera. " +
                        "Puedes restaurarlo o eliminarlo permanentemente antes de crear uno nuevo.");
            } else {
                throw new IllegalArgumentException("Ya existe un producto con el nombre: " + name);
            }
        });
    }
}
