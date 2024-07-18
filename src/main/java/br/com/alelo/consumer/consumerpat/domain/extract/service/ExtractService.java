package br.com.alelo.consumer.consumerpat.domain.extract.service;

import br.com.alelo.consumer.consumerpat.domain.card.OperationType;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCard;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataSetBalance;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.extract.entity.Extract;
import br.com.alelo.consumer.consumerpat.domain.extract.repository.ExtractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExtractService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ExtractRepository extractRepository;

    /* Salvar na tabela de extratos o crédito realizado no cartão */
    public void createCreditExtract(DataSetBalance data) {
        var card = cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.cardType());
        var extract = new Extract(card, data.value(), OperationType.CREDIT, data.cardType());
        extractRepository.save(extract);
    }

    /* Salvar na tabela de extratos o crédito realizado no cartão */
    public void createDebitExtract(DataBuyWithCard data, BigDecimal debitedValue) {
        var card = cardRepository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType());
        var extract = new Extract(card, debitedValue, OperationType.DEBIT, data.establishmentType(), data.establishmentName(), data.productDescription());
        extractRepository.save(extract);
    }
}
