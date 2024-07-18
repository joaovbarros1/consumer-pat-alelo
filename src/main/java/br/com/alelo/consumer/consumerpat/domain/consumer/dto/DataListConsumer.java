package br.com.alelo.consumer.consumerpat.domain.consumer.dto;

import br.com.alelo.consumer.consumerpat.domain.address.entity.Address;
import br.com.alelo.consumer.consumerpat.domain.card.dto.ListCardData;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import br.com.alelo.consumer.consumerpat.domain.contact.entity.Contact;

import java.time.LocalDate;
import java.util.List;

public record DataListConsumer(
        Long id,
        String name,
        String documentNumber,
        LocalDate birthDate,
        Contact contact,
        Address address,
        List<ListCardData> cards
) {

    public DataListConsumer(Consumer consumer) {
        this(consumer.getId(), consumer.getName(), consumer.getDocumentNumber(), consumer.getBirthDate(),
                consumer.getContact(), consumer.getAddress(), consumer.getCards().stream().map(ListCardData::new).toList());
    }
}
