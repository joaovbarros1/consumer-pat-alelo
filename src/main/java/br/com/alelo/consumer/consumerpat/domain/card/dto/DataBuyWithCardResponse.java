package br.com.alelo.consumer.consumerpat.domain.card.dto;

import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DataBuyWithCardResponse(
        @NotNull(message = "Não pode ser nulo")
        Consumer consumer,
        @NotNull(message = "Não pode ser nulo")
        BigDecimal debitedValue
) {
}
