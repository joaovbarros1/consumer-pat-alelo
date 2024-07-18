package br.com.alelo.consumer.consumerpat.domain.card.strategy.balance;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataSetBalance;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.balance.exception.SetCardBalanceStrategyException;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SetFoodCardBalanceStrategyTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private SetFoodCardBalanceStrategy setFoodCardBalanceStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSetBalance_Success() {

        DataSetBalance data = getDataSetBalance();
        var consumer = createConsumer();
        var cardData = new CardData(AleloServiceType.FOOD, "0000111122223333");
        var card = new Card(cardData);
        card.setConsumer(consumer);

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.cardType())).thenReturn(card);

        Consumer result = setFoodCardBalanceStrategy.setBalance(data);

        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_EVEN), card.getCardBalance());

        assertEquals(consumer, result);

        verify(cardRepository, times(1)).findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.cardType());
    }

    @Test
    void testSetBalance_CardNotFound() {

        DataSetBalance data = getDataSetBalance();

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.cardType())).thenThrow(new EntityNotFoundException());

        SetCardBalanceStrategyException exception = assertThrows(SetCardBalanceStrategyException.class, () -> {
            setFoodCardBalanceStrategy.setBalance(data);
        });

        assertEquals("Não foi encontrado nenhum usuário com o número do cartão informado", exception.getMessage());
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

    private DataSetBalance getDataSetBalance() {
        return new DataSetBalance(AleloServiceType.FOOD, "0000111122223333", BigDecimal.valueOf(100));
    }
}
