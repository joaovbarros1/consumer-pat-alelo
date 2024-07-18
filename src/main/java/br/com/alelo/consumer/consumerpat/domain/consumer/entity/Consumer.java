package br.com.alelo.consumer.consumerpat.domain.consumer.entity;

import br.com.alelo.consumer.consumerpat.domain.address.entity.Address;
import br.com.alelo.consumer.consumerpat.domain.card.entity.Card;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataCreateConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataUpdateConsumer;
import br.com.alelo.consumer.consumerpat.domain.contact.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "CONSUMER")
@Entity(name = "Consumer")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Consumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String documentNumber;
    private LocalDate birthDate;
    private Boolean active;

    //contacts
    @Embedded
    private Contact contact;

    //Address
    @Embedded
    private Address address;

    //cards
    @OneToMany(mappedBy = "consumer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    public Consumer(DataCreateConsumer data) {
        this.name = data.name();
        this.documentNumber = data.documentNumber();
        this.birthDate = data.birthDate();
        this.contact = new Contact(data.contact());
        this.address = new Address(data.address());
        this.active = true;
    }

    public void addCard(Card card) {
        card.setConsumer(this);
        this.cards.add(card);
    }

    public void update(DataUpdateConsumer data) {
        if (data.name() != null) {
            this.name = data.name();
        }
        if (data.documentNumber() != null) {
            this.documentNumber = data.documentNumber();
        }
        if (data.birthDate() != null) {
            this.birthDate = data.birthDate();
        }
        if (data.contact() != null) {
            this.contact.update(data.contact());
        }
        if (data.address() != null) {
            this.address.update(data.address());
        }
    }

    public void delete() {
        this.active = false;
    }
}
