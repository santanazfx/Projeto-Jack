package br.com.jack.pedidos.api;

import br.com.jack.pedidos.model.Product;
import br.com.jack.pedidos.service.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/products")
    public List<Product> products(@RequestParam(required = false) String category,
                                  @RequestParam(required = false) String search) {
        return catalogService.findAll(category, search);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return catalogService.categories();
    }
}
