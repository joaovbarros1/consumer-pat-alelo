package br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase;

import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCard;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCardResponse;
import br.com.alelo.consumer.consumerpat.domain.card.exception.CardBalanceException;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.card.service.CardService;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase.exception.BuyWithCardStrategyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

@Component
public class BuyWithFuelCardStrategy implements BuyWithCardStrategy {

    @Autowired
    private CardRepository repository;

    @Autowired
    private CardService service;

    private final BigDecimal INCREASE = BigDecimal.valueOf(0.35);

    @Override
    public DataBuyWithCardResponse buyWithCard(DataBuyWithCard data) {
        try {
            var card = repository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.establishmentType());
            if (card == null) {
                throw new EntityNotFoundException();
            }
            /* Usando o cartão combustível, o cliente paga um acréscimo de 35% */
            var multiplicationFactor = BigDecimal.ONE.add(INCREASE);
            var purchaseValue = data.amount().multiply(multiplicationFactor);
            service.purchaseWithCard(card, purchaseValue);
            return new DataBuyWithCardResponse(card.getConsumer(), purchaseValue);
        } catch (CardBalanceException ex) {
            throw new CardBalanceException(ex.getMessage());
        } catch (EntityNotFoundException ex) {
            throw new BuyWithCardStrategyException("Não foi encontrado nenhum usuário com o número de cartão informado");
        }
    }
}
