package br.com.alelo.consumer.consumerpat.domain.extract.entity;

import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.OperationType;
import br.com.alelo.consumer.consumerpat.domain.establishment.EstablishmentName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "EXTRACT")
@Entity(name = "Extract")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Extract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstablishmentName establishmentName;

    @Enumerated(EnumType.STRING)
    private AleloServiceType establishmentType;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    private String productDescription;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    private BigDecimal amount;

    public Extract(Card card, BigDecimal value, OperationType operationType, AleloServiceType establishmentType) {
        this.card = card;
        this.amount = value;
        this.operationType = operationType;
        this.establishmentName = null;
        this.establishmentType = establishmentType;
        this.productDescription = null;
        this.date = LocalDateTime.now();
    }

    public Extract(Card card, BigDecimal value, OperationType operationType, AleloServiceType establishmentType, EstablishmentName establishmentName, String productDescription) {
        this.card = card;
        this.amount = value;
        this.operationType = operationType;
        this.establishmentName = establishmentName;
        this.establishmentType = establishmentType;
        this.productDescription = productDescription;
        this.date = LocalDateTime.now();
    }
}
