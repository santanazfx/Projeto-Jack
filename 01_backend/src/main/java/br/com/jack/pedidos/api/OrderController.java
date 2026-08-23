package br.com.jack.pedidos.api;

import br.com.jack.pedidos.model.OrderRequest;
import br.com.jack.pedidos.model.OrderResponse;
import br.com.jack.pedidos.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{trackingCode}")
    public OrderResponse find(@PathVariable String trackingCode) {
        return orderService.find(trackingCode);
    }
}
