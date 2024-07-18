package br.com.alelo.consumer.consumerpat.domain.extract.service;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.OperationType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCard;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataSetBalance;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.establishment.EstablishmentName;
import br.com.alelo.consumer.consumerpat.domain.extract.entity.Extract;
import br.com.alelo.consumer.consumerpat.domain.extract.repository.ExtractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class ExtractServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ExtractRepository extractRepository;

    @InjectMocks
    private ExtractService extractService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCreditExtract_Success() {
        DataSetBalance data = new DataSetBalance(AleloServiceType.FOOD, "0000111122223333", BigDecimal.valueOf(100.00));
        Card card = new Card(new CardData(data.cardType(), data.cardNumber()));

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.cardType())).thenReturn(card);

        extractService.createCreditExtract(data);

        Extract expectedExtract = new Extract(card, data.value(), OperationType.CREDIT, data.cardType());
        verify(extractRepository, times(1)).save(expectedExtract);
    }

    @Test
    void testCreateDebitExtract_Success() {
        DataBuyWithCard data = new DataBuyWithCard(AleloServiceType.FOOD, EstablishmentName.MERCADOSAOJOSE, "0000111122223333", "Arroz", BigDecimal.valueOf(100.00));
        BigDecimal debitedValue = BigDecimal.valueOf(50.00);
        Card card = new Card(new CardData(data.establishmentType(), data.cardNumber()));

        when(cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType())).thenReturn(card);

        extractService.createDebitExtract(data, debitedValue);

        Extract expectedExtract = new Extract(card, debitedValue, OperationType.DEBIT, data.establishmentType(), data.establishmentName(), data.productDescription());
        verify(extractRepository, times(1)).save(expectedExtract);
    }
}
