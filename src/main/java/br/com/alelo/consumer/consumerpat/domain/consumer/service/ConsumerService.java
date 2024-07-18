package br.com.alelo.consumer.consumerpat.domain.consumer.service;

import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardNumberAlreadyExistentException;
import br.com.alelo.consumer.consumerpat.domain.card.service.CardService;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataUpdateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ConsumerService {

    @Autowired
    private ConsumerRepository repository;

    @Autowired
    private CardService cardService;

    /* Cria um cliente, salva no repository e chama o cardService para criar os cartões desse cliente */
    public Consumer createConsumer(DataCreateConsumer data) {
        var consumer = new Consumer(data);
        try {
            addCardToConsumer(consumer, data.cards());
            repository.save(consumer);
            return consumer;
        } catch (CardNumberAlreadyExistentException ex) {
            throw new CardNumberAlreadyExistentException(ex.getMessage());
        }
    }

    /* Retorna um cliente para detalhamento caso ele esteja ativo, caso não, lança uma EntityNotFoundException */
    public Consumer detailConsumer(Long id) {
        var consumer = repository.findByIdAndActiveTrue(id);
        if (consumer == null) {
            throw new EntityNotFoundException();
        }
        return consumer;
    }

    /* Faz o update de um cliente caso ele esteja ativo, caso não, lança uma EntityNotFoundException */
    public Consumer updateConsumer(DataUpdateConsumer data) {
        var consumer = repository.findByIdAndActiveTrue(data.id());
        if (consumer == null) {
            throw new EntityNotFoundException();
        }
        consumer.update(data);
        return consumer;
    }

    /* Deleta um cliente e deleta todos os cartões daquele cliente */
    public void deleteConsumer(Consumer consumer) {
        consumer.delete();
        cardService.deleteAllCards(consumer.getCards());
    }

    /* Adiciona um ou mais cartões para determinado cliente */
    private void addCardToConsumer(Consumer consumer, List<CardData> cards) {
        cardService.addCartao(consumer, cards);
    }
}
