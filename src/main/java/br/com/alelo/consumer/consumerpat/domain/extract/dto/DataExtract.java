package br.com.alelo.consumer.consumerpat.domain.extract.dto;

import br.com.alelo.consumer.consumerpat.domain.card.OperationType;
import br.com.alelo.consumer.consumerpat.domain.establishment.EstablishmentName;
import br.com.alelo.consumer.consumerpat.domain.extract.entity.Extract;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DataExtract(LocalDateTime date, OperationType operationType, BigDecimal value,
                          EstablishmentName establishmentName, String productDescription) {

    public DataExtract(Extract extract) {
        this(extract.getDate(), extract.getOperationType(), extract.getAmount(),
                extract.getEstablishmentName(), extract.getProductDescription());
    }
}
