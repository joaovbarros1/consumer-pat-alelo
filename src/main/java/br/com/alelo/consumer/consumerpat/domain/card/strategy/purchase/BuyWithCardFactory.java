package br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase.exception.BuyWithCardStrategyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyWithCardFactory {

    @Autowired
    private BuyWithFoodCardStrategy buyWithFoodCardStrategy;

    @Autowired
    private BuyWithDrugstoreCardStrategy buyWithDrugstoreCardStrategy;

    @Autowired
    private BuyWithFuelCardStrategy buyWithFuelCardStrategy;

    public BuyWithCardStrategy getStrategy(String type) {
        try {
            var typeEnum = AleloServiceType.valueOf(type);
            return switch(typeEnum) {
                case FOOD -> buyWithFoodCardStrategy;
                case DRUGSTORE -> buyWithDrugstoreCardStrategy;
                case FUEL -> buyWithFuelCardStrategy;
            };
        } catch (IllegalArgumentException ex) {
            throw new BuyWithCardStrategyException("Tipo de cartão não encontrado");
        }
    }
}
