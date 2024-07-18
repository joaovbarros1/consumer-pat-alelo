package br.com.alelo.consumer.consumerpat.controller;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataListConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataUpdateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.repository.ConsumerRepository;
import br.com.alelo.consumer.consumerpat.domain.consumer.service.ConsumerService;
import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ConsumerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DataCreateConsumer> dataCreateConsumerJson;

    @Autowired
    private JacksonTester<DataListConsumer> dataListConsumerJson;

    @Autowired
    private JacksonTester<DataUpdateConsumer> dataUpdateConsumerJson;

    @MockBean
    private ConsumerRepository repository;

    @MockBean
    private ConsumerService service;

    @Test
    @DisplayName("Deveria listar consumidores com paginação")
    void list() throws Exception {
        var pagination = PageRequest.of(0, 20, Sort.by("name"));

        var consumer = createConsumer();
        var consumers = List.of(consumer);
        var page = new PageImpl<>(consumers, pagination, consumers.size());

        Mockito.when(repository.findAllByActiveTrue(pagination)).thenReturn(page);

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/consumer-pat/v1/consumers")
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", "name"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString();
        var pageResult = new PageImpl<>(
                List.of(new DataListConsumer(consumers.get(0))),
                pagination,
                consumers.size()
        );

        var expectedOutputJson = dataListConsumerJson.write(pageResult.getContent().get(0)).getJson();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(jsonResponse).contains(expectedOutputJson);
    }

    @Test
    @DisplayName("Deveria devolver código http 400 quando informações estão inválidas")
    void create_setting1() throws Exception {
        var response = mockMvc.perform(post("/consumer-pat/v1/consumers"))
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver código http 201 quando informações estão válidas")
    void create_setting2() throws Exception {
        var dataCreateConsumer = new DataCreateConsumer(
                "John Doe",
                "123.456.789-00",
                LocalDate.of(1990, 1, 1),
                new ContactData("123456789", "33314532", "66698745", "john.doe@example.com"),
                new AddressData("Street", "City", "State", "Country", "12345678"),
                List.of(new CardData(AleloServiceType.FOOD, "0000111122223333"))
        );

        var consumer = createConsumer();
        var dataListConsumer = new DataListConsumer(consumer);
        Mockito.when(service.createConsumer(dataCreateConsumer)).thenReturn(consumer);

        var response = mockMvc.perform(post("/consumer-pat/v1/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dataCreateConsumerJson.write(dataCreateConsumer).getJson()))
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var expectedOutputJson = dataListConsumerJson.write(dataListConsumer).getJson();

        Assertions.assertThat(response.getContentAsString()).isEqualTo(expectedOutputJson);
    }

    @Test
    @DisplayName("Deveria devolver detalhes de um consumidor")
    void detail() throws Exception {
        Long id = 1L;

        var consumer = createConsumer();
        Mockito.when(service.detailConsumer(id)).thenReturn(consumer);

        var response = mockMvc.perform(get("/consumer-pat/v1/consumers/{id}", id))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(jsonResponse).isEqualTo(dataListConsumerJson.write(new DataListConsumer(consumer)).getJson());
    }

    @Test
    @DisplayName("Deveria atualizar um consumidor")
    void update() throws Exception {

        var consumer = createConsumer();

        var dataUpdateConsumer = new DataUpdateConsumer(
                1L,
                "John Updated",
                "987.654.321-00",
                LocalDate.of(1991, 2, 2),
                new ContactData("123456789", "33314532", "66698745", "john.doe@example.com"),
                new AddressData("Updated Street", "Updated City", "Updated State", "Updated Country", "98765432")
        );

        Mockito.when(service.updateConsumer(dataUpdateConsumer)).thenReturn(consumer);

        var response = mockMvc.perform(put("/consumer-pat/v1/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dataUpdateConsumerJson.write(dataUpdateConsumer).getJson()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(jsonResponse).isEqualTo(dataListConsumerJson.write(new DataListConsumer(consumer)).getJson());
    }

    @Test
    @DisplayName("Deveria deletar consumidor com sucesso")
    void delete() throws Exception {
        Long consumerId = 1L;

        Mockito.doNothing().when(service).deleteConsumer(Mockito.any());

        var response = mockMvc.perform(MockMvcRequestBuilders.delete("/consumer-pat/v1/consumers/{id}", consumerId))
                .andExpect(status().isNoContent())
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
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
