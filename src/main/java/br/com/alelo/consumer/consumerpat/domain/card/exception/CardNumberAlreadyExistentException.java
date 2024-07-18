package br.com.alelo.consumer.consumerpat.domain.card.exception;

public class CardNumberAlreadyExistentException extends RuntimeException{

    public CardNumberAlreadyExistentException(String message) {
        super(message);
    }
}
