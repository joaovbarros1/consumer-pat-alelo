package br.com.alelo.consumer.consumerpat.domain.card.service;

import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardBalanceException;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardNumberAlreadyExistentException;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CardService {

    @Autowired
    private ConsumerRepository consumerRepository;

    /* Adiciona os cartões informados para o consumidor em questão */
    public Consumer addCartao(Consumer consumer, List<CardData> cards) {
        if (!consumer.getActive()) {
            throw new EntityNotFoundException();
        }
        try {
            for (CardData cardData : cards) {
                Card card = new Card(cardData);
                consumer.addCard(card);
            }
            consumerRepository.save(consumer);
            return consumer;
        } catch (EntityNotFoundException ex) {
            throw new EntityNotFoundException();
        } catch (PersistenceException | DataIntegrityViolationException ex) {
            throw new CardNumberAlreadyExistentException("O número do cartão deve ser único (não pode ser igual a outro já cadastrado)");
        }
    }

    /* Comprar com um cartão específico - faz a validação de saldo insuficiente */
    public void purchaseWithCard(Card card, BigDecimal value) {
        if (card.getCardBalance().compareTo(value) < 0) {
            throw new CardBalanceException("Saldo insuficiente para compra");
        }
        card.purchaseWithCardBalance(value);
    }

    /* Deletar logicamente todos os cartões em caso de deletar o cliente */
    public void deleteAllCards(List<Card> cards) {
        for (Card card : cards) {
            card.delete();
        }
    }
}
