package br.com.alelo.consumer.consumerpat.domain.card.strategy.balance;

import br.com.alelo.consumer.consumerpat.domain.card.dto.DataSetBalance;
import br.com.alelo.consumer.consumerpat.domain.card.repository.CardRepository;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.balance.exception.SetCardBalanceStrategyException;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
public class SetFoodCardBalanceStrategy implements SetCardBalanceStrategy {

    @Autowired
    private CardRepository repository;

    @Override
    public Consumer setBalance(DataSetBalance data) {
        try {
            var card = repository.findByCardNumberAndCardTypeAndActiveTrue(data.cardNumber(), data.cardType());
            if (card == null) {
                throw new EntityNotFoundException();
            }
            card.setCardBalance(data.value());
            return card.getConsumer();
        } catch (EntityNotFoundException ex) {
            throw new SetCardBalanceStrategyException("Não foi encontrado nenhum usuário com o número do cartão informado");
        }
    }
}
