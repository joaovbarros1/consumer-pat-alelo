package br.com.alelo.consumer.consumerpat.domain.card.strategy.balance;

import br.com.alelo.consumer.consumerpat.domain.card.dto.DataSetBalance;
import br.com.alelo.consumer.consumerpat.domain.consumer.entity.Consumer;

public interface SetCardBalanceStrategy {

    Consumer setBalance(DataSetBalance data);
}
