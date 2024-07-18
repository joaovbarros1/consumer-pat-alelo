package br.com.alelo.consumer.consumerpat.domain.card.dto;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.establishment.EstablishmentName;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

public record DataBuyWithCard(

        @NotNull(message = "Não pode ser nulo")
        AleloServiceType establishmentType,
        @NotNull(message = "Não pode ser nulo")
        EstablishmentName establishmentName,
        @NotBlank(message = "Não pode ser nulo")
        @Pattern(regexp = "\\d{16}", message = "O número do cartão é composto por 16 dígitos")
        String cardNumber,
        @NotBlank(message = "Não pode ser nulo")
        String productDescription,
        @NotNull(message = "Não pode ser nulo")
        BigDecimal amount
){
}
