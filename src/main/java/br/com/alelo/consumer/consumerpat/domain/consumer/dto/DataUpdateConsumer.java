package br.com.alelo.consumer.consumerpat.domain.consumer.dto;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public record DataUpdateConsumer(
        @NotNull(message = "Não pode ser nulo")
        Long id,
        String name,
        @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}")
        String documentNumber,
        LocalDate birthDate,
        @Valid
        ContactData contact,
        @Valid
        AddressData address
        ) {
}
