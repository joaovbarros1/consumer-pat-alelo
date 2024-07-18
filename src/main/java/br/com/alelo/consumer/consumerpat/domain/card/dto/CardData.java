package br.com.alelo.consumer.consumerpat.domain.card.dto;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record CardData(

        @NotNull(message = "Não pode ser nulo")
        AleloServiceType cardType,
        @NotBlank(message = "Não pode ser nulo")
        @Pattern(regexp = "\\d{16}", message = "O número do cartão é composto por 16 dígitos")
        String cardNumber
) {
}
