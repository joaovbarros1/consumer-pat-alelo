package br.com.alelo.consumer.consumerpat.domain.card.strategy.balance;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.balance.exception.SetCardBalanceStrategyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetCardBalanceFactory {

    @Autowired
    private SetFoodCardBalanceStrategy setFoodCardBalanceStrategy;

    @Autowired
    private SetFuelCardBalanceStrategy setFuelCardBalanceStrategy;

    @Autowired
    private SetDrugstoreCardBalanceStrategy setDrugstoreCardBalanceStrategy;

    public SetCardBalanceStrategy getStrategy(String type) {
        try {
            var typeEnum = AleloServiceType.valueOf(type);
            return switch (typeEnum) {
                case FOOD -> setFoodCardBalanceStrategy;
                case DRUGSTORE -> setDrugstoreCardBalanceStrategy;
                case FUEL -> setFuelCardBalanceStrategy;
            };
        } catch (IllegalArgumentException ex) {
            throw new SetCardBalanceStrategyException("Tipo de cartão não encontrado");
        }
    }
}
