package br.com.alelo.consumer.consumerpat.domain.card.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public record AddCardData(
        @NotNull(message = "Não pode ser nulo")
        Long consumerId,
        @NotNull(message = "Não pode ser nulo")
        @Valid
        List<CardData> cards
) {
}
