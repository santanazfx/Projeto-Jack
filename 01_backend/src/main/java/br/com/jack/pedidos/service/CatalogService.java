package br.com.jack.pedidos.service;

import br.com.jack.pedidos.model.Product;
import br.com.jack.pedidos.domain.ProductEntity;
import br.com.jack.pedidos.error.NotFoundException;
import br.com.jack.pedidos.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {
    private final ProductRepository productRepository;

    public CatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public List<Product> findAll(String category, String search) {
        return productRepository.findByAvailableTrueOrderByDisplayOrderAsc().stream()
                .filter(product -> category == null || category.isBlank() || category.equalsIgnoreCase("Todos") || product.getCategory().getName().equalsIgnoreCase(category) || product.getBread().equalsIgnoreCase(category))
                .filter(product -> search == null || search.isBlank() || matches(product, search))
                .map(this::toModel)
                .toList();
    }

    public List<String> categories() {
        return List.of("Todos", "Pão de hambúrguer", "Pão francês mata fome", "Pão brioche");
    }

    @Transactional
    public Product findById(Long id) {
        return toModel(findAvailableEntity(id));
    }

    public ProductEntity findAvailableEntity(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado."));
        if (!product.isAvailable()) {
            throw new IllegalStateException("Este produto está indisponível no momento.");
        }
        return product;
    }

    private Product toModel(ProductEntity product) {
        return new Product(product.getId(), product.getName(), product.getDescription(), product.getCategory().getName(), product.getBread(), product.getPrice(), product.getImage(), product.getColor(), product.isFeatured(), product.isAvailable());
    }

    private boolean matches(ProductEntity product, String search) {
        String text = search.toLowerCase();
        return product.getName().toLowerCase().contains(text)
                || product.getDescription().toLowerCase().contains(text)
                || product.getCategory().getName().toLowerCase().contains(text)
                || product.getBread().toLowerCase().contains(text);
    }
}
