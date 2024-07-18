package br.com.alelo.consumer.consumerpat.domain.card.service;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardBalanceException;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardNumberAlreadyExistentException;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.repository.ConsumerRepository;
import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private ConsumerRepository consumerRepository;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddCartao_Success() {
        Consumer consumer = createConsumer();
        List<CardData> cards = new ArrayList<>();
        cards.add(new CardData(AleloServiceType.FOOD, "1234567890123456"));

        when(consumerRepository.save(any(Consumer.class))).thenReturn(consumer);

        Consumer result = cardService.addCartao(consumer, cards);

        assertEquals(1, result.getCards().size());
        verify(consumerRepository, times(1)).save(consumer);
    }

    @Test
    void testAddCartao_CardNumberAlreadyExistentException() {
        Consumer consumer = createConsumer();
        List<CardData> cards = new ArrayList<>();
        cards.add(new CardData(AleloServiceType.FOOD, "1234567890123456"));

        when(consumerRepository.save(any(Consumer.class))).thenThrow(DataIntegrityViolationException.class);

        Exception ex = assertThrows(CardNumberAlreadyExistentException.class, () -> {
            cardService.addCartao(consumer, cards);
        });

        verify(consumerRepository, times(1)).save(consumer);
        Assertions.assertThat(ex.getMessage()).isEqualTo("O número do cartão deve ser único (não pode ser igual a outro já cadastrado)");
    }

    @Test
    void testPurchaseWithCard_Success() {
        Card card = new Card(new CardData(AleloServiceType.FOOD, "0000111122223333"));
        card.setCardBalance(new BigDecimal("100.00"));

        cardService.purchaseWithCard(card, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("50.00"), card.getCardBalance());
    }

    @Test
    void testPurchaseWithCard_CardBalanceException() {
        Card card = new Card(new CardData(AleloServiceType.FOOD, "0000111122223333"));
        card.setCardBalance(new BigDecimal("30.00"));

        Exception ex = assertThrows(CardBalanceException.class, () -> {
            cardService.purchaseWithCard(card, new BigDecimal("50.00"));
        });

        Assertions.assertThat(ex.getMessage()).isEqualTo("Saldo insuficiente para compra");
    }

    @Test
    void testDeleteAllCards() {
        List<Card> cards = new ArrayList<>();
        Card card1 = new Card(new CardData(AleloServiceType.FOOD, "0000111122223333"));
        Card card2 = new Card(new CardData(AleloServiceType.DRUGSTORE, "1111222233334444"));
        cards.add(card1);
        cards.add(card2);

        cardService.deleteAllCards(cards);

        assertFalse(card1.getActive());
        assertFalse(card2.getActive());
    }

    private Consumer createConsumer() {
        var dataCreateConsumer = new DataCreateConsumer(
                "John Doe",
                "123.456.789-00",
                LocalDate.of(1990, 1, 1),
                new ContactData("123456789", "33314532", "66698745", "john.doe@example.com"),
                new AddressData("Street", "City", "State", "Country", "12345678"),
                List.of(new CardData(AleloServiceType.FOOD, "0000111122223333"))
        );

        return new Consumer(dataCreateConsumer);
    }
}
