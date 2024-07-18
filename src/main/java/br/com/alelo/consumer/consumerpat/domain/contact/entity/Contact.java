package br.com.alelo.consumer.consumerpat.domain.contact.entity;

import br.com.alelo.consumer.consumerpat.domain.contact.dto.ContactData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    private String mobilePhoneNumber;
    private String residencePhoneNumber;
    private String phoneNumber;
    private String email;

    public Contact (ContactData data) {
        this.mobilePhoneNumber = data.mobilePhoneNumber();
        this.residencePhoneNumber = data.residencePhoneNumber();
        this.phoneNumber = data.phoneNumber();
        this.email = data.email();
    }

    public void update(ContactData data) {
        if(data.mobilePhoneNumber() != null) {
            this.mobilePhoneNumber = data.mobilePhoneNumber();
        }
        if(data.residencePhoneNumber() != null) {
            this.residencePhoneNumber = data.residencePhoneNumber();
        }
        if(data.phoneNumber() != null) {
            this.phoneNumber = data.phoneNumber();
        }
        if(data.email() != null) {
            this.email = data.email();
        }
    }
}
