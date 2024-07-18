package br.com.alelo.consumer.consumerpat.domain.extract.dto;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;

import java.math.BigDecimal;
import java.util.List;

public record DataListExtract(
        String consumerName,
        AleloServiceType cardType,
        String cardNumber,
        Boolean active,
        List<DataExtract> extracts,
        BigDecimal cardBalance,
        Integer currentPage,
        Integer totalPages,
        Long totalItems
) {
}
