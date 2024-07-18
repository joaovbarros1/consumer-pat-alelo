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

class BuyWithDrugstoreCardStrategyTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private BuyWithDrugstoreCardStrategy buyWithDrugstoreCardStrategy;

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

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType())).thenReturn(card);
        doNothing().when(cardService).purchaseWithCard(card, data.amount());

        DataBuyWithCardResponse response = buyWithDrugstoreCardStrategy.buyWithCard(data);

        assertEquals(consumer, response.consumer());
        assertEquals(data.amount(), response.debitedValue());

        verify(cardRepository, times(1)).findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType());
        verify(cardService, times(1)).purchaseWithCard(card, data.amount());
    }

    @Test
    void testBuyWithCard_CardBalanceException() {
        DataBuyWithCard data = getDataBuyWithCard();
        Card card = new Card(new CardData(data.establishmentType(), data.cardNumber()));

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType())).thenReturn(card);
        doThrow(new CardBalanceException("Saldo insuficiente para compra.")).when(cardService).purchaseWithCard(card, data.amount());

        CardBalanceException exception = assertThrows(CardBalanceException.class, () -> {
            buyWithDrugstoreCardStrategy.buyWithCard(data);
        });

        assertEquals("Saldo insuficiente para compra.", exception.getMessage());
    }

    @Test
    void testBuyWithCard_CardNotFound() {
        DataBuyWithCard data = getDataBuyWithCard();

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType())).thenThrow(new EntityNotFoundException());

        BuyWithCardStrategyException exception = assertThrows(BuyWithCardStrategyException.class, () -> {
            buyWithDrugstoreCardStrategy.buyWithCard(data);
        });

        assertEquals("Não foi encontrado nenhum usuário com o número de cartão informado", exception.getMessage());
    }

    private Consumer createConsumer() {
        return new Consumer();
    }

    private DataBuyWithCard getDataBuyWithCard() {
        return new DataBuyWithCard(AleloServiceType.DRUGSTORE, EstablishmentName.FARMACIAHELIO, "0000111122223333", "Dipirona", BigDecimal.valueOf(100));
    }
}
