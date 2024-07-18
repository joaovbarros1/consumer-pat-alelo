package br.com.alelo.consumer.consumerpat.domain.card.exception;

public class CardBalanceException extends RuntimeException{
    public CardBalanceException(String message) {
        super(message);
    }
}
