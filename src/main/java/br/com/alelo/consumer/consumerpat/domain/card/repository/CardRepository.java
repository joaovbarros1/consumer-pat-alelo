package br.com.alelo.consumer.consumerpat.domain.card.repository;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    Card findByCardNumberAndCardTypeAndActiveTrue(String cardNumber, AleloServiceType cardType);

}
