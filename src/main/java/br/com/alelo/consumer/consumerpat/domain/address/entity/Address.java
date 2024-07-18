package br.com.alelo.consumer.consumerpat.domain.address.entity;

import br.com.alelo.consumer.consumerpat.domain.address.dto.AddressData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String street;
    private String number;
    private String city;
    private String country;
    private String portalCode;

    public Address(AddressData data) {
        this.street = data.street();
        this.number = data.number();
        this.city = data.city();
        this.country = data.country();
        this.portalCode = data.portalCode();
    }

    public void update(AddressData data) {
        if (data.street() != null) {
            this.street = data.street();
        }
        if (data.number() != null) {
            this.number = data.number();
        }
        if (data.city() != null) {
            this.city = data.city();
        }
        if (data.country() != null) {
            this.country = data.country();
        }
        if (data.portalCode() != null) {
            this.portalCode = data.portalCode();
        }
    }
}
