package br.com.alelo.consumer.consumerpat.controller;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.OperationType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;
import br.com.alelo.consumer.consumerpat.domain.extract.dto.DataExtract;
import br.com.alelo.consumer.consumerpat.domain.extract.dto.DataListExtract;
import br.com.alelo.consumer.consumerpat.domain.extract.entity.Extract;
import br.com.alelo.consumer.consumerpat.domain.extract.repository.ExtractRepository;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class ExtractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DataListExtract> dataListExtractJson;

    @MockBean
    private ExtractRepository extractRepository;

    @MockBean
    private CardRepository cardRepository;

    @Test
    @DisplayName("Deveria listar o extrato de um cartão específico com paginação")
    void listCardExtract() throws Exception {
        Long cardId = 1L;
        Pageable pagination = PageRequest.of(0, 20, Sort.by("date"));

        Consumer consumer = createConsumer();

        ReflectionTestUtils.setField(consumer, "id", cardId);

        var cardData = new CardData(AleloServiceType.FOOD, "0000111122223333");
        var card = new Card(cardData);
        card.setConsumer(consumer);

        ReflectionTestUtils.setField(card, "id", cardId);

        Extract extract = new Extract(card, BigDecimal.valueOf(50), OperationType.CREDIT, AleloServiceType.FOOD);
        var page = new PageImpl<>(List.of(extract), pagination, 1);

        Mockito.when(extractRepository.findAllByCardId(cardId, pagination)).thenReturn(page);
        Mockito.when(cardRepository.getReferenceById(cardId)).thenReturn(card);

        var response = mockMvc.perform(get("/consumer-pat/v1/extracts/{cardId}", cardId)
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", "date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var expectedOutput = dataListExtractJson.write(new DataListExtract(
                card.getConsumer().getName(),
                card.getCardType(),
                card.getCardNumber(),
                card.getActive(),
                List.of(new DataExtract(extract)),
                card.getCardBalance(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        )).getJson();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(expectedOutput);
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
