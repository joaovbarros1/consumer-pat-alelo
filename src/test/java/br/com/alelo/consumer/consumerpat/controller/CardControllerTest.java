package br.com.alelo.consumer.consumerpat.controller;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.*;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.card.service.CardService;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.balance.SetCardBalanceFactory;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.balance.SetCardBalanceStrategy;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase.BuyWithCardFactory;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase.BuyWithCardStrategy;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataListConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.repository.ConsumerRepository;
import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;
import br.com.alelo.consumer.consumerpat.domain.establishment.EstablishmentName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<AddCardData> addCardDataJson;

    @Autowired
    private JacksonTester<DataSetBalance> dataSetBalanceJson;

    @Autowired
    private JacksonTester<DataBuyWithCard> dataBuyWithCardJson;

    @Autowired
    private JacksonTester<DataListConsumer> dataListConsumerJson;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private ConsumerRepository consumerRepository;

    @MockBean
    private CardService cardService;

    @MockBean
    private SetCardBalanceFactory setCardBalanceFactory;

    @MockBean
    private BuyWithCardFactory buyWithCardFactory;

    @Test
    @DisplayName("Deveria adicionar cartão ao consumidor")
    void addCardToConsumer_setting1() throws Exception {

        var response = mockMvc.perform(post("/consumer-pat/v1/cards"))
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria adicionar cartão ao consumidor")
    void addCardToConsumer_setting2() throws Exception {

        var cardData = new CardData(AleloServiceType.FOOD, "0000111122223333");
        var consumer = createConsumer();

        // Configurando o ID manualmente
        ReflectionTestUtils.setField(consumer, "id", 1L);

        var addCardData = new AddCardData(consumer.getId(), List.of(cardData));

        Mockito.when(consumerRepository.getReferenceById(addCardData.consumerId())).thenReturn(consumer);
        Mockito.when(cardService.addCartao(consumer, addCardData.cards())).thenReturn(consumer);

        var response = mockMvc.perform(post("/consumer-pat/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCardDataJson.write(addCardData).getJson()))
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var expectedOutputJson = dataListConsumerJson.write(new DataListConsumer(consumer)).getJson();
        Assertions.assertThat(response.getContentAsString()).isEqualTo(expectedOutputJson);
    }

    @Test
    @DisplayName("Deveria deletar um cartão")
    void deleteCard() throws Exception {
        Long cardId = 1L;
        var cardData = new CardData(AleloServiceType.FOOD, "0000111122223333");
        var card = new Card(cardData);

        Mockito.when(cardRepository.getReferenceById(cardId)).thenReturn(card);

        var response = mockMvc.perform(MockMvcRequestBuilders.delete("/consumer-pat/v1/cards/{id}", cardId))
                .andExpect(status().isNoContent())
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Deveria definir saldo no cartão")
    void setCardBalance() throws Exception {
        var dataSetBalance = new DataSetBalance(AleloServiceType.FOOD, "0000111122223333", BigDecimal.TEN);
        var consumer = createConsumer();
        var strategy = Mockito.mock(SetCardBalanceStrategy.class);

        Mockito.when(setCardBalanceFactory.getStrategy(dataSetBalance.cardType().toString())).thenReturn(strategy);
        Mockito.when(strategy.setBalance(dataSetBalance)).thenReturn(consumer);

        var response = mockMvc.perform(post("/consumer-pat/v1/cards/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dataSetBalanceJson.write(dataSetBalance).getJson()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        var expectedOutputJson = dataListConsumerJson.write(new DataListConsumer(consumer)).getJson();
        Assertions.assertThat(response.getContentAsString()).isEqualTo(expectedOutputJson);
    }

    @Test
    @DisplayName("Deveria realizar compra com cartão")
    void buyWithCard() throws Exception {
        var consumer = createConsumer();
        var dataBuyWithCard = new DataBuyWithCard(AleloServiceType.FOOD, EstablishmentName.MERCADOSAOJOSE, "0000111122223333", "Arroz", BigDecimal.TEN);
        var strategy = Mockito.mock(BuyWithCardStrategy.class);
        var dataBuyResponse = new DataBuyWithCardResponse(consumer, BigDecimal.TEN);

        Mockito.when(buyWithCardFactory.getStrategy(dataBuyWithCard.establishmentType().toString())).thenReturn(strategy);
        Mockito.when(strategy.buyWithCard(dataBuyWithCard)).thenReturn(dataBuyResponse);

        var response = mockMvc.perform(post("/consumer-pat/v1/cards/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dataBuyWithCardJson.write(dataBuyWithCard).getJson()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        var expectedOutputJson = dataListConsumerJson.write(new DataListConsumer(consumer)).getJson();
        Assertions.assertThat(response.getContentAsString()).isEqualTo(expectedOutputJson);
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
