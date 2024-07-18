package br.com.alelo.consumer.consumerpat.domain.contact.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record ContactData(

        @NotBlank(message = "Não pode ser nulo")
        @Pattern(regexp = "\\d{9}", message = "O celular deve ter 9 dígitos")
        String mobilePhoneNumber,
        @NotBlank(message = "Não pode ser nulo")
        @Pattern(regexp = "\\d{8}", message = "O telefone deve ter 8 dígitos")
        String residencePhoneNumber,
        @NotBlank(message = "Não pode ser nulo")
        @Pattern(regexp = "\\d{8}", message = "O telefone deve ter 8 dígitos")
        String phoneNumber,
        @NotBlank(message = "Não pode ser nulo")
        @Email(message = "Deve ter o formato de um e-mail")
        String email
) {
}
