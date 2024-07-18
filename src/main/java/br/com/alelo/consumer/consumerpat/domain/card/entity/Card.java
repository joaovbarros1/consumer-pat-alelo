package br.com.alelo.consumer.consumerpat.domain.card.entity;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Table(name = "CARD")
@Entity(name = "Card")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private AleloServiceType cardType;
    @Column(unique = true)
    private String cardNumber;
    private BigDecimal cardBalance;
    private Boolean active;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    @JsonIgnore
    private Consumer consumer;

    public Card(CardData data) {
        this.cardType = data.cardType();
        this.cardNumber = data.cardNumber();
        this.cardBalance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        this.active = true;
    }

    public void delete() {
        this.active = false;
    }

    public void setCardBalance(BigDecimal value) {
        this.cardBalance = this.cardBalance.add(value).setScale(2, RoundingMode.HALF_EVEN);
    }

    public void purchaseWithCardBalance(BigDecimal value) {
        this.cardBalance = this.cardBalance.subtract(value).setScale(2, RoundingMode.HALF_EVEN);
    }
}
