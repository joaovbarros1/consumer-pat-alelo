package br.com.alelo.consumer.consumerpat.domain.address.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record AddressData(

        @NotBlank(message = "Não pode ser nulo")
        String street,
        @NotBlank(message = "Não pode ser nulo")
        String number,
        @NotBlank(message = "Não pode ser nulo")
        String city,
        @NotBlank(message = "Não pode ser nulo")
        String country,
        @NotBlank(message = "Não pode ser nulo")
        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 números")
        String portalCode
) {
}
