package br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.purchase.exception.BuyWithCardStrategyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuyWithCardFactoryTest {

    @Mock
    private BuyWithFoodCardStrategy buyWithFoodCardStrategy;

    @Mock
    private BuyWithFuelCardStrategy buyWithFuelCardStrategy;

    @Mock
    private BuyWithDrugstoreCardStrategy buyWithDrugstoreCardStrategy;

    @InjectMocks
    private BuyWithCardFactory buyWithCardFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetStrategy_Food() {
        BuyWithCardStrategy strategy = buyWithCardFactory.getStrategy(AleloServiceType.FOOD.toString());
        assertEquals(buyWithFoodCardStrategy, strategy);
    }

    @Test
    void testGetStrategy_Drugstore() {
        BuyWithCardStrategy strategy = buyWithCardFactory.getStrategy(AleloServiceType.DRUGSTORE.toString());
        assertEquals(buyWithDrugstoreCardStrategy, strategy);
    }

    @Test
    void testGetStrategy_Fuel() {
        BuyWithCardStrategy strategy = buyWithCardFactory.getStrategy(AleloServiceType.FUEL.toString());
        assertEquals(buyWithFuelCardStrategy, strategy);
    }

    @Test
    void testGetStrategy_InvalidType() {

        BuyWithCardStrategyException exception = assertThrows(BuyWithCardStrategyException.class, () -> {
            buyWithCardFactory.getStrategy("INVALID");
        });
        assertEquals("Tipo de cartão não encontrado", exception.getMessage());
    }
}
