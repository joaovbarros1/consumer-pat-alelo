package br.com.alelo.consumer.consumerpat.controller;

import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.extract.dto.DataExtract;
import br.com.alelo.consumer.consumerpat.domain.extract.dto.DataListExtract;
import br.com.alelo.consumer.consumerpat.domain.extract.repository.ExtractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer-pat/v1/extracts")
public class ExtractController {

    @Autowired
    private ExtractRepository extractRepository;

    @Autowired
    private CardRepository cardRepository;

    /* Lista o extrato de um cartão específico, passando seu id por path variable */
    @GetMapping("/{cardId}")
    public ResponseEntity<DataListExtract> listCardExtract(@PathVariable Long cardId,
                                                           @PageableDefault(size = 20, page = 0, sort = {"date"}) Pageable pagination) {
        var page = extractRepository.findAllByCardId(cardId, pagination).map(DataExtract::new);

        var card = cardRepository.getReferenceById(cardId);

        return ResponseEntity.ok(new DataListExtract(card.getConsumer().getName(), card.getCardType(),
                card.getCardNumber(), card.getActive(), page.getContent(), card.getCardBalance(), page.getNumber(),
                page.getTotalPages(), page.getTotalElements()));
    }
}
