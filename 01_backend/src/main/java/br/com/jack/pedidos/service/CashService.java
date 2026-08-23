package br.com.jack.pedidos.service;

import br.com.jack.pedidos.domain.*;
import br.com.jack.pedidos.error.BusinessException;
import br.com.jack.pedidos.error.NotFoundException;
import br.com.jack.pedidos.model.*;
import br.com.jack.pedidos.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CashService {
    private final TabRepository tabs;
    private final TabItemRepository tabItems;
    private final ProductRepository products;
    private final CustomerRepository customers;
    private final AddonRepository addons;
    private final PaymentRepository payments;
    private final JdbcTemplate jdbc;

    public CashService(TabRepository tabs, TabItemRepository tabItems, ProductRepository products, CustomerRepository customers, AddonRepository addons, PaymentRepository payments, JdbcTemplate jdbc) {
        this.tabs=tabs; this.tabItems=tabItems; this.products=products; this.customers=customers; this.addons=addons; this.payments=payments; this.jdbc=jdbc;
    }

    @Transactional
    public List<TabResponse> list(String query, String status) {
        String rawText = trim(query);
        final String text = rawText == null ? null : rawText.toLowerCase();
        return tabs.findAllByOrderByOpenedAtDesc().stream()
                .filter(tab -> status == null || status.isBlank() || tab.getStatus().name().equalsIgnoreCase(status))
                .filter(tab -> text == null || tab.getCode().toLowerCase().contains(text) || tab.getCustomer()!=null && tab.getCustomer().getName().toLowerCase().contains(text))
                .map(this::response).toList();
    }

    @Transactional
    public TabResponse find(Long id) { return response(requiredTab(id)); }

    @Transactional
    public TabResponse create(CreateTabRequest request) {
        String code = trim(request.code());
        if (code == null) code = "C-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        if (tabs.existsByCodeIgnoreCase(code)) throw new BusinessException("Já existe uma comanda com esse número.");
        CustomerEntity customer = customer(request.customerName(), request.phone());
        TabEntity tab = tabs.save(new TabEntity(code.toUpperCase(), customer, request.serviceType()==null ? "TABLE" : request.serviceType(), trim(request.notes())));
        audit("TAB", tab.getId(), "CREATED", null, code);
        return response(tab);
    }

    @Transactional
    public TabResponse addItem(Long tabId, TabItemInput request) {
        TabEntity tab = requiredOpenTab(tabId);
        ProductEntity product = products.findById(request.productId()).orElseThrow(() -> new NotFoundException("Produto não encontrado."));
        if (!product.isAvailable()) throw new BusinessException("Produto indisponível.");
        List<String> selected = safe(request.extras());
        Set<String> allowed = product.getAddons().stream().filter(AddonEntity::isActive).map(AddonEntity::getName).collect(Collectors.toSet());
        if (!allowed.containsAll(selected)) throw new BusinessException("Um adicional selecionado está indisponível.");
        List<AddonEntity> addonEntities = selected.isEmpty() ? List.of() : addons.findByNameInAndActiveTrue(selected);
        BigDecimal unitPrice = product.getPrice().add(addonEntities.stream().map(AddonEntity::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        TabItemEntity item = new TabItemEntity(product, unitPrice, request.quantity(), String.join(", ", selected), String.join(", ", safe(request.removals())), trim(request.notes()));
        tab.addItem(item);
        tabs.save(tab);
        audit("TAB_ITEM", item.getId(), "ADDED", null, product.getName());
        return response(tab);
    }

    @Transactional
    public TabResponse updateItem(Long tabId, Long itemId, UpdateTabItemRequest request) {
        TabEntity tab = requiredOpenTab(tabId);
        TabItemEntity item = requiredItem(tab, itemId);
        if (!item.isActive()) throw new BusinessException("Item cancelado não pode ser alterado.");
        item.update(request.quantity(), trim(request.notes())); tab.recalculate(); tabs.save(tab);
        audit("TAB_ITEM", itemId, "UPDATED", null, "Quantidade: " + request.quantity());
        return response(tab);
    }

    @Transactional
    public TabResponse removeDraftItem(Long tabId, Long itemId) {
        TabEntity tab=requiredOpenTab(tabId); TabItemEntity item=requiredItem(tab,itemId);
        tab.removeItem(item); tabs.save(tab); audit("TAB_ITEM", itemId, "DELETED", null, item.getProductName()); return response(tab);
    }

    @Transactional
    public TabResponse cancelItem(Long tabId, Long itemId, ReasonRequest request) {
        TabEntity tab=requiredOpenTab(tabId); TabItemEntity item=requiredItem(tab,itemId);
        item.cancel(request.reason().trim()); tab.recalculate(); tabs.save(tab); audit("TAB_ITEM", itemId, "CANCELLED", null, request.reason()); return response(tab);
    }

    @Transactional
    public TabResponse cancel(Long id, ReasonRequest request) {
        TabEntity tab=requiredOpenTab(id); tab.cancel(); tabs.save(tab); audit("TAB", id, "CANCELLED", null, request.reason()); return response(tab);
    }

    @Transactional
    public void deleteEmpty(Long id) {
        TabEntity tab=requiredOpenTab(id); if (!tab.getItems().isEmpty()) throw new BusinessException("Comandas com itens devem ser canceladas para preservar o histórico."); tabs.delete(tab);
    }

    @Transactional
    public TabResponse finalizeTab(Long id, FinalizeTabRequest request) {
        TabEntity tab=requiredOpenTab(id); if (tab.getTotal().compareTo(BigDecimal.ZERO)<=0) throw new BusinessException("Adicione itens antes de finalizar a comanda.");
        tab.close(); tabs.save(tab); payments.save(new PaymentEntity(tab, request.paymentMethod(), tab.getTotal())); audit("TAB", id, "CLOSED", null, request.paymentMethod()); return response(tab);
    }

    @Transactional
    public Product setAvailability(Long id, boolean available, String reason) {
        ProductEntity product=products.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado.")); product.setAvailability(available, trim(reason)); products.save(product); audit("PRODUCT", id, available?"AVAILABLE":"SOLD_OUT", null, product.getName()); return product(product);
    }

    @Transactional
    public List<CashProductResponse> cashProducts() { return products.findAllByOrderByDisplayOrderAsc().stream().map(this::cashProduct).toList(); }

    private CustomerEntity customer(String name, String phone) {
        String cleanPhone=normalizePhone(phone), cleanName=trim(name);
        if (cleanName==null && cleanPhone==null) return null;
        String finalName=cleanName==null?"Cliente presencial":cleanName;
        if (cleanPhone!=null) return customers.findByPhone(cleanPhone).map(found->{found.updateName(finalName);return found;}).orElseGet(()->customers.save(new CustomerEntity(finalName,cleanPhone)));
        return customers.save(new CustomerEntity(finalName,null));
    }
    private TabEntity requiredTab(Long id){return tabs.findDetailedById(id).orElseThrow(()->new NotFoundException("Comanda não encontrada."));}
    private TabEntity requiredOpenTab(Long id){TabEntity tab=requiredTab(id);if(tab.getStatus()!=TabStatus.OPEN)throw new BusinessException("Somente comandas abertas podem ser alteradas.");return tab;}
    private TabItemEntity requiredItem(TabEntity tab,Long id){return tab.getItems().stream().filter(item->item.getId().equals(id)).findFirst().orElseThrow(()->new NotFoundException("Item não encontrado nesta comanda."));}
    private List<String> safe(List<String> values){return values==null?List.of():values.stream().filter(Objects::nonNull).map(String::trim).filter(v->!v.isBlank()).distinct().toList();}
    private String trim(String value){return value==null||value.isBlank()?null:value.trim();}
    private String normalizePhone(String value){return value==null||value.isBlank()?null:value.replaceAll("[^0-9+]","");}
    private void audit(String entity,Long id,String action,String before,String after){jdbc.update("insert into audit_logs (entity_type, entity_id, action, previous_data, new_data) values (?, ?, ?, ?, ?)",entity,String.valueOf(id),action,before,after);}
    private TabResponse response(TabEntity tab){List<TabItemResponse> items=tab.getItems().stream().map(item->new TabItemResponse(item.getId(),item.getProductName(),item.getUnitPrice(),item.getQuantity(),item.getExtrasSnapshot(),item.getRemovalsSnapshot(),item.getNotes(),item.getLineTotal(),item.getStatus().name())).toList();int count=tab.getItems().stream().filter(TabItemEntity::isActive).mapToInt(TabItemEntity::getQuantity).sum();return new TabResponse(tab.getId(),tab.getCode(),tab.getCustomer()==null?null:tab.getCustomer().getName(),tab.getCustomer()==null?null:tab.getCustomer().getPhone(),tab.getServiceType(),tab.getStatus().name(),tab.getOpenedAt(),tab.getClosedAt(),count,tab.getSubtotal(),tab.getTotal(),tab.getNotes(),items);}
    private Product product(ProductEntity p){return new Product(p.getId(),p.getName(),p.getDescription(),p.getCategory().getName(),p.getBread(),p.getPrice(),p.getImage(),p.getColor(),p.isFeatured(),p.isAvailable());}
    private CashProductResponse cashProduct(ProductEntity p){return new CashProductResponse(p.getId(),p.getName(),p.getDescription(),p.getCategory().getName(),p.getBread(),p.getPrice(),p.getImage(),p.isAvailable(),p.getUnavailableReason(),p.getAddons().stream().filter(AddonEntity::isActive).sorted(Comparator.comparing(AddonEntity::getName)).map(a->new AddonOptionResponse(a.getName(),a.getPrice())).toList());}
}
