package br.com.jack.pedidos.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequest(
        @NotBlank(message = "Informe seu nome.") String customerName,
        @NotBlank(message = "Informe seu WhatsApp.") String phone,
        @NotBlank(message = "Informe o tipo de atendimento.") String serviceType,
        String tableNumber,
        @Valid AddressRequest address,
        String paymentMethod,
        String pickupTime,
        String notes,
        @NotEmpty(message = "Adicione pelo menos um item ao pedido.") List<@Valid OrderItemRequest> items
) { }
