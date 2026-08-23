package br.com.jack.pedidos.api;

import br.com.jack.pedidos.model.*;
import br.com.jack.pedidos.service.CashService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cash")
public class CashController {
    private final CashService service;
    public CashController(CashService service){this.service=service;}
    @GetMapping("/tabs") public List<TabResponse> list(@RequestParam(required=false) String query,@RequestParam(required=false) String status){return service.list(query,status);}
    @GetMapping("/tabs/{id}") public TabResponse find(@PathVariable Long id){return service.find(id);}
    @PostMapping("/tabs") @ResponseStatus(HttpStatus.CREATED) public TabResponse create(@Valid @RequestBody CreateTabRequest request){return service.create(request);}
    @PostMapping("/tabs/{id}/items") public TabResponse addItem(@PathVariable Long id,@Valid @RequestBody TabItemInput request){return service.addItem(id,request);}
    @PatchMapping("/tabs/{id}/items/{itemId}") public TabResponse updateItem(@PathVariable Long id,@PathVariable Long itemId,@Valid @RequestBody UpdateTabItemRequest request){return service.updateItem(id,itemId,request);}
    @DeleteMapping("/tabs/{id}/items/{itemId}") public TabResponse removeItem(@PathVariable Long id,@PathVariable Long itemId){return service.removeDraftItem(id,itemId);}
    @PostMapping("/tabs/{id}/items/{itemId}/cancel") public TabResponse cancelItem(@PathVariable Long id,@PathVariable Long itemId,@Valid @RequestBody ReasonRequest request){return service.cancelItem(id,itemId,request);}
    @PostMapping("/tabs/{id}/cancel") public TabResponse cancel(@PathVariable Long id,@Valid @RequestBody ReasonRequest request){return service.cancel(id,request);}
    @DeleteMapping("/tabs/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.deleteEmpty(id);}
    @PostMapping("/tabs/{id}/finalize") public TabResponse finalizeTab(@PathVariable Long id,@Valid @RequestBody FinalizeTabRequest request){return service.finalizeTab(id,request);}
    @GetMapping("/products") public List<CashProductResponse> products(){return service.cashProducts();}
    @PatchMapping("/products/{id}/availability") public Product availability(@PathVariable Long id,@Valid @RequestBody AvailabilityRequest request){return service.setAvailability(id,request.available(),request.reason());}
}
