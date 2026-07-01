package com.stockly.api.service;

import com.stockly.api.dto.ProductRequest;
import com.stockly.api.dto.ProductResponse;
import com.stockly.api.entity.Product;
import com.stockly.api.exception.DuplicateResourceException;
import com.stockly.api.exception.ResourceNotFoundException;
import com.stockly.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Já existe um produto com o SKU: " + request.getSku());
        }

        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .description(request.getDescription())
                .category(request.getCategory())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();

        return toResponse(productRepository.save(product));
    }

    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    public ProductResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getOrThrow(id);

        if (!product.getSku().equalsIgnoreCase(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Já existe um produto com o SKU: " + request.getSku());
        }

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = getOrThrow(id);
        productRepository.delete(product);
    }

    public Page<ProductResponse> search(String name, String category, Pageable pageable) {
        boolean hasName = StringUtils.hasText(name);
        boolean hasCategory = StringUtils.hasText(category);

        Page<Product> result;
        if (hasName && hasCategory) {
            result = productRepository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(name, category, pageable);
        } else if (hasName) {
            result = productRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (hasCategory) {
            result = productRepository.findByCategoryContainingIgnoreCase(category, pageable);
        } else {
            result = productRepository.findAll(pageable);
        }

        return result.map(this::toResponse);
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .sku(p.getSku())
                .description(p.getDescription())
                .category(p.getCategory())
                .quantity(p.getQuantity())
                .price(p.getPrice())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
