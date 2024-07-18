package br.com.alelo.consumer.consumerpat.infra.exception;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.exception.AleloServiceTypeNotFound;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardBalanceException;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardNumberAlreadyExistentException;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.balance.exception.SetCardBalanceStrategyException;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase.exception.BuyWithCardStrategyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    /* Tratar erros de chamadas inválidas das URLs e registros de bd não encontrados*/
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> error404() {
        return ResponseEntity.notFound().build();
    }

    /* Tratar erros de contrato */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> error400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /* Tratar erros do strategy do SetCardBalance - não encontrou nenhum strategy com o tipo informado ou usuário não encontrado*/
    @ExceptionHandler(SetCardBalanceStrategyException.class)
    public ResponseEntity<String> errorSetCardBalance(SetCardBalanceStrategyException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + ex.getMessage());
    }

    /* Tratar erros do strategy do BuyWithCard - não encontrou nenhum strategy com o tipo informado ou usuário não encontrado */
    @ExceptionHandler(BuyWithCardStrategyException.class)
    public ResponseEntity<String> errorBuyWithCard(BuyWithCardStrategyException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + ex.getMessage());
    }

    /* Tratar erros de cadastro de cartão com número já existente na base de dados*/
    @ExceptionHandler(CardNumberAlreadyExistentException.class)
    public ResponseEntity<String> errorCardNumberAlreadyExistent(CardNumberAlreadyExistentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + ex.getMessage());
    }

    /* Tratar erros de informação de serviço da Alelo não existente - diferentes de FOOD, FUEL ou DRUGSTORE */
    @ExceptionHandler(AleloServiceTypeNotFound.class)
    public ResponseEntity<String> errorAleloServiceTypeNotFound(AleloServiceTypeNotFound ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + ex.getMessage());
    }

    /* Tratar erros de saldo insuficiente nos cartões para compras */
    @ExceptionHandler(CardBalanceException.class)
    public ResponseEntity<String> errorInsufficientCardBalance(CardBalanceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: " + ex.getMessage());
    }

    /* Tratar erros de validações dos campos para cadastro e atualizações */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorData>> error400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(ValidationErrorData::new).toList());
    }

    /* Tratar erros genéricos - servidor */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> error500(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + ex.getMessage());
    }
}
