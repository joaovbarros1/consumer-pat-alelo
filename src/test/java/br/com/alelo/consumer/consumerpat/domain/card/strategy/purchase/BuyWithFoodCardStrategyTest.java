package br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCard;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCardResponse;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardBalanceException;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.card.service.CardService;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase.exception.BuyWithCardStrategyException;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.establishment.EstablishmentName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BuyWithFoodCardStrategyTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private BuyWithFoodCardStrategy buyWithFoodCardStrategy;

    private static final BigDecimal DISCOUNT = BigDecimal.valueOf(0.10);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testBuyWithCard_Success() {
        DataBuyWithCard data = getDataBuyWithCard();
        Consumer consumer = createConsumer();
        Card card = new Card(new CardData(data.establishmentType(), data.cardNumber()));
        card.setConsumer(consumer);

        BigDecimal expectedPurchaseValue = data.amount().multiply(BigDecimal.ONE.subtract(DISCOUNT));

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType())).thenReturn(card);
        doNothing().when(cardService).purchaseWithCard(card, expectedPurchaseValue);

        DataBuyWithCardResponse response = buyWithFoodCardStrategy.buyWithCard(data);

        assertEquals(consumer, response.consumer());
        assertEquals(expectedPurchaseValue, response.debitedValue());

        verify(cardRepository, times(1)).findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType());
        verify(cardService, times(1)).purchaseWithCard(card, expectedPurchaseValue);
    }

    @Test
    void testBuyWithCard_CardBalanceException() {
        DataBuyWithCard data = getDataBuyWithCard();
        Card card = new Card(new CardData(data.establishmentType(), data.cardNumber()));

        BigDecimal expectedPurchaseValue = data.amount().multiply(BigDecimal.ONE.subtract(DISCOUNT));

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType())).thenReturn(card);
        doThrow(new CardBalanceException("Saldo insuficiente para compra.")).when(cardService).purchaseWithCard(card, expectedPurchaseValue);

        CardBalanceException exception = assertThrows(CardBalanceException.class, () -> {
            buyWithFoodCardStrategy.buyWithCard(data);
        });

        assertEquals("Saldo insuficiente para compra.", exception.getMessage());
    }

    @Test
    void testBuyWithCard_CardNotFound() {
        DataBuyWithCard data = getDataBuyWithCard();

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType())).thenThrow(new EntityNotFoundException());

        BuyWithCardStrategyException exception = assertThrows(BuyWithCardStrategyException.class, () -> {
            buyWithFoodCardStrategy.buyWithCard(data);
        });

        assertEquals("Não foi encontrado nenhum usuário com o número de cartão informado", exception.getMessage());
    }

    private Consumer createConsumer() {
        return new Consumer();
    }

    private DataBuyWithCard getDataBuyWithCard() {
        return new DataBuyWithCard(AleloServiceType.FOOD, EstablishmentName.MERCADOSAOJOSE, "0000111122223333", "Arroz", BigDecimal.valueOf(100));
    }
}
