package br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase;

import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCard;
import br.com.alelo.consumer.consumerpat.domain.card.dto.DataBuyWithCardResponse;

public interface BuyWithCardStrategy {

    DataBuyWithCardResponse buyWithCard(DataBuyWithCard data);
}
