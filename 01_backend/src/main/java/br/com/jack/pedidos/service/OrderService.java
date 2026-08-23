package br.com.jack.pedidos.service;

import br.com.jack.pedidos.domain.*;
import br.com.jack.pedidos.error.BusinessException;
import br.com.jack.pedidos.error.NotFoundException;
import br.com.jack.pedidos.model.*;
import br.com.jack.pedidos.repository.AddressRepository;
import br.com.jack.pedidos.repository.CustomerRepository;
import br.com.jack.pedidos.repository.OrderRepository;
import br.com.jack.pedidos.repository.AddonRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final CatalogService catalogService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final AddonRepository addonRepository;

    public OrderService(CatalogService catalogService, OrderRepository orderRepository, CustomerRepository customerRepository, AddressRepository addressRepository, AddonRepository addonRepository) {
        this.catalogService = catalogService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.addonRepository = addonRepository;
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        OrderType type = OrderType.from(request.serviceType());
        validateServiceDetails(request, type);
        CustomerEntity customer = customerRepository.findByPhone(normalizePhone(request.phone()))
                .map(found -> { found.updateName(request.customerName().trim()); return found; })
                .orElseGet(() -> customerRepository.save(new CustomerEntity(request.customerName().trim(), normalizePhone(request.phone()))));
        AddressEntity address = type == OrderType.DELIVERY ? saveAddress(customer, request.address()) : null;
        List<OrderItemEntity> items = request.items().stream().map(this::toOrderItem).toList();
        BigDecimal subtotal = items.stream().map(OrderItemEntity::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal deliveryFee = type == OrderType.DELIVERY ? new BigDecimal("6.00") : BigDecimal.ZERO;
        OrderEntity order = new OrderEntity(nextTrackingCode(), customer, address, type, PaymentMethod.from(request.paymentMethod()),
                normalizeBlank(request.tableNumber()), normalizeBlank(request.notes()), subtotal, deliveryFee, type == OrderType.DELIVERY ? "35 a 50 min" : "20 a 30 min");
        items.forEach(order::addItem);
        order.registerInitialStatus();
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse find(String trackingCode) {
        return toResponse(orderRepository.findByTrackingCodeIgnoreCase(trackingCode.trim())
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado.")));
    }

    private OrderItemEntity toOrderItem(OrderItemRequest item) {
        ProductEntity product = catalogService.findAvailableEntity(item.productId());
        List<String> extras = safe(item.extras());
        Set<String> productAddons = product.getAddons().stream().filter(AddonEntity::isActive).map(AddonEntity::getName).collect(Collectors.toSet());
        if (!productAddons.containsAll(extras)) {
            throw new BusinessException("Um adicional selecionado não está disponível para " + product.getName() + ".");
        }
        List<AddonEntity> addonEntities = extras.isEmpty() ? List.of() : addonRepository.findByNameInAndActiveTrue(extras);
        if (addonEntities.size() != extras.size()) {
            throw new BusinessException("Um adicional selecionado está indisponível.");
        }
        List<String> removals = safe(item.removals());
        boolean invalidRemoval = removals.stream().anyMatch(removal -> !product.getDescription().toLowerCase(Locale.ROOT).contains(removal.toLowerCase(Locale.ROOT)));
        if (invalidRemoval) {
            throw new BusinessException("Só é possível remover ingredientes presentes no produto.");
        }
        BigDecimal unitPrice = product.getPrice().add(addonEntities.stream().map(AddonEntity::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        return new OrderItemEntity(product, product.getName(), unitPrice, item.quantity(), String.join(", ", extras), String.join(", ", removals), normalizeBlank(item.notes()));
    }

    private AddressEntity saveAddress(CustomerEntity customer, AddressRequest request) {
        return addressRepository.save(new AddressEntity(customer, request.zipCode().trim(), request.street().trim(), request.number().trim(), normalizeBlank(request.complement()), request.neighborhood().trim(), normalizeBlank(request.city()), normalizeBlank(request.state()), normalizeBlank(request.reference())));
    }

    private void validateServiceDetails(OrderRequest request, OrderType type) {
        if (type == null) throw new BusinessException("Tipo de atendimento inválido.");
        if (type == OrderType.DELIVERY && request.address() == null) throw new BusinessException("Informe o endereço de entrega.");
        if (type == OrderType.TABLE && (request.tableNumber() == null || request.tableNumber().isBlank())) throw new BusinessException("Informe o número da mesa.");
    }

    private String nextTrackingCode() { return "JACK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT); }
    private List<String> safe(List<String> values) { return values == null ? List.of() : values.stream().filter(value -> value != null && !value.isBlank()).map(String::trim).distinct().toList(); }
    private String normalizeBlank(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String normalizePhone(String value) { return value.replaceAll("[^0-9+]", ""); }
    private OrderResponse toResponse(OrderEntity order) {
        return new OrderResponse(order.getTrackingCode(), order.getStatus().label(order.getOrderType()), order.getEstimatedTime(), order.getTotal().setScale(2, RoundingMode.HALF_UP), order.getCreatedAt(), order.getOrderType().label(), order.getTableNumber(), order.getAddress() == null ? null : order.getAddress().display());
    }
}
