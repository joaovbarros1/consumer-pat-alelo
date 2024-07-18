package br.com.alelo.consumer.consumerpat.domain.consumer.dto;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import br.com.alelo.consumer.consumerpat.domain.card.dto.CardData;
import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

public record DataCreateConsumer(

        @NotBlank(message = "Não pode ser nulo")
        String name,
        @NotBlank(message = "Não pode ser nulo")
        @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}")
        String documentNumber,
        @NotNull(message = "Não pode ser nulo")
        LocalDate birthDate,
        @NotNull(message = "Não pode ser nulo")
        @Valid
        ContactData contact,
        @NotNull(message = "Não pode ser nulo")
        @Valid
        AddressData address,
        @NotNull(message = "Não pode ser nulo")
        @Valid
        List<CardData> cards
) {
}
