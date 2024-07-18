package br.com.alelo.consumer.consumerpat.domain.card.dto;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;

import java.math.BigDecimal;

public record ListCardData(
        Long id,
        AleloServiceType cardType,
        String cardNumber,
        BigDecimal cardBalance,
        Boolean active
) {
    public ListCardData(Card card) {
        this(card.getId(), card.getCardType(), card.getCardNumber(), card.getCardBalance(), card.getActive());
    }
}
