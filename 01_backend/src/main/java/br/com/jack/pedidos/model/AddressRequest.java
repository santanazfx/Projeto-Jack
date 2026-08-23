package br.com.jack.pedidos.model;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank(message = "Informe o CEP.") String zipCode,
        @NotBlank(message = "Informe a rua.") String street,
        @NotBlank(message = "Informe o número.") String number,
        String complement,
        @NotBlank(message = "Informe o bairro.") String neighborhood,
        String city,
        String state,
        String reference
) { }
