package br.com.alelo.consumer.consumerpat.controller;

import br.com.alelo.consumer.consumerpat.domain.card.dto.AddCardData;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCard;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataSetBalance;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.card.service.CardService;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.balance.SetCardBalanceFactory;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase.BuyWithCardFactory;
import br.com.alelo.consumer.consumerpat.domain.consumer.dto.DataListConsumer;
import br.com.alelo.consumer.consumerpat.domain.consumer.repository.ConsumerRepository;
import br.com.alelo.consumer.consumerpat.domain.extract.service.ExtractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/consumer-pat/v1/cards")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private ExtractService extractService;

    @Autowired
    private SetCardBalanceFactory setCardBalanceFactory;

    @Autowired
    private BuyWithCardFactory buyWithCardFactory;

    /* Adiciona um ou mais cartões para um determinado cliente */
    @PostMapping
    public ResponseEntity<DataListConsumer> addCardToConsumer(@Valid @RequestBody AddCardData data, UriComponentsBuilder uriBuilder) {
        var consumer = consumerRepository.getReferenceById(data.consumerId());
        cardService.addCartao(consumer, data.cards());

        var uri = uriBuilder.path("/consumer-pat/v1/consumers/{id}").buildAndExpand(consumer.getId()).toUri();
        return ResponseEntity.created(uri).body(new DataListConsumer(consumer));
    }

    /* Exclusão lógica de um cartão - passando o id do cartão como pathVariable*/
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        var card = cardRepository.getReferenceById(id);
        card.delete();

        return ResponseEntity.noContent().build();
    }

    /* Inserir saldo no cartão */
    @PostMapping("/balance")
    @Transactional
    public ResponseEntity<DataListConsumer> setCardBalance(@RequestBody @Valid DataSetBalance data) {
        var strategy = setCardBalanceFactory.getStrategy(data.cardType().toString());
        var consumer = strategy.setBalance(data);
        extractService.createCreditExtract(data);

        return ResponseEntity.ok(new DataListConsumer(consumer));
    }

    /* Fazer uma compra com um cartão - só pode comprar com FOOD onde é FOOD, etc. Faz a validação de saldo antes da compra */
    @PostMapping("/purchase")
    @Transactional
    public ResponseEntity<DataListConsumer> buyWithCard(@RequestBody @Valid DataBuyWithCard data) {
        var strategy = buyWithCardFactory.getStrategy(data.establishmentType().toString());
        var dataBuyResponse = strategy.buyWithCard(data);
        extractService.createDebitExtract(data, dataBuyResponse.debitedValue());

        return ResponseEntity.ok(new DataListConsumer(dataBuyResponse.consumer()));
    }
}
