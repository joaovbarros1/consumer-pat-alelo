package br.com.alelo.consumer.consumerpat.domain.consumer.service;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardNumberAlreadyExistentException;
import br.com.alelo.consumer.consumerpat.domain.card.service.CardService;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataUpdateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.repository.ConsumerRepository;
import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@Nested
class ConsumerServiceTest {

    @Mock
    private ConsumerRepository repository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private ConsumerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateConsumer_Success() {
        var dataCreateConsumer = getDataCreateConsumer();
        var consumer = new Consumer(dataCreateConsumer);

        when(repository.save(any(Consumer.class))).thenReturn(consumer);

        Consumer result = service.createConsumer(dataCreateConsumer);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(consumer.getName(), result.getName());
        verify(repository, times(1)).save(any(Consumer.class));
        verify(cardService, times(1)).addCartao(any(Consumer.class), anyList());
    }

    @Test
    void testCreateConsumer_CardNumberAlreadyExistentException() {
        var dataCreateConsumer = getDataCreateConsumer();
        var consumer = new Consumer(dataCreateConsumer);

        doThrow(new CardNumberAlreadyExistentException("Número do cartão já existente")).when(cardService).addCartao(any(Consumer.class), anyList());

        CardNumberAlreadyExistentException ex = Assertions.assertThrows(CardNumberAlreadyExistentException.class, () -> {
            service.createConsumer(dataCreateConsumer);
        });

        Assertions.assertEquals("Número do cartão já existente", ex.getMessage());
        verify(repository, never()).save(any(Consumer.class));
        verify(cardService, times(1)).addCartao(any(Consumer.class), anyList());
    }

    @Test
    void testDetailConsumer_Success() {
        Long id = 1L;
        var dataCreateConsumer = getDataCreateConsumer();
        var consumer = new Consumer(dataCreateConsumer);

        when(repository.findByIdAndActiveTrue(id)).thenReturn(consumer);

        Consumer result = service.detailConsumer(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(consumer, result);
        verify(repository, times(1)).findByIdAndActiveTrue(id);
    }

    @Test
    void testDetailConsumer_EntityNotFoundException() {
        Long id = 1L;

        when(repository.findByIdAndActiveTrue(id)).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.detailConsumer(id);
        });

        verify(repository, times(1)).findByIdAndActiveTrue(id);
    }

    @Test
    void testUpdateConsumer_Success() {
        var dataUpdateConsumer = getDataUpdateConsumer();
        var dataCreateConsumer = getDataCreateConsumer();
        var consumer = new Consumer(dataCreateConsumer);

        when(repository.findByIdAndActiveTrue(dataUpdateConsumer.id())).thenReturn(consumer);

        Consumer result = service.updateConsumer(dataUpdateConsumer);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(consumer, result);
        verify(repository, times(1)).findByIdAndActiveTrue(dataUpdateConsumer.id());
    }

    @Test
    void testUpdateConsumer_EntityNotFoundException() {
        var dataUpdateConsumer = getDataUpdateConsumer();

        when(repository.findByIdAndActiveTrue(dataUpdateConsumer.id())).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.updateConsumer(dataUpdateConsumer);
        });

        verify(repository, times(1)).findByIdAndActiveTrue(dataUpdateConsumer.id());
    }

    @Test
    void testDeleteConsumer_Success() {
        var dataCreateConsumer = getDataCreateConsumer();
        var consumer = new Consumer(dataCreateConsumer);
        consumer.addCard(getCardList().get(0));

        doNothing().when(cardService).deleteAllCards(anyList());

        service.deleteConsumer(consumer);

        Assertions.assertFalse(consumer.getActive());
        verify(cardService, times(1)).deleteAllCards(anyList());
    }

    private DataCreateConsumer getDataCreateConsumer() {
        return new DataCreateConsumer(
                "John Doe",
                "123.456.789-00",
                LocalDate.of(1990, 1, 1),
                new ContactData("123456789", "33314532", "66698745", "john.doe@example.com"),
                new AddressData("Street", "City", "State", "Country", "12345678"),
                List.of(new CardData(AleloServiceType.FOOD, "0000111122223333"))
        );
    }

    private DataUpdateConsumer getDataUpdateConsumer() {
        return new DataUpdateConsumer(
                1L,
                "John Doe",
                "123.456.789-00",
                LocalDate.of(1990, 1, 1),
                new ContactData("123456789", "33314532", "66698745", "john.doe@example.com"),
                new AddressData("Street", "City", "State", "Country", "12345678")
        );
    }

    private List<Card> getCardList() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(new CardData(AleloServiceType.FOOD, "0000111122223333")));
        return cards;
    }
}
