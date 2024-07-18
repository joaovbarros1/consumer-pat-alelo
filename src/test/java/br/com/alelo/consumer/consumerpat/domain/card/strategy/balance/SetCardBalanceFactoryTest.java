package br.com.alelo.consumer.consumerpat.domain.card.strategy.balance;

import br.com.alelo.consumer.consumerpat.domain.aleloServiceType.AleloServiceType;
import br.com.alelo.consumer.consumerpat.domain.card.strategy.balance.exception.SetCardBalanceStrategyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetCardBalanceFactoryTest {

    @Mock
    private SetFoodCardBalanceStrategy setFoodCardBalanceStrategy;

    @Mock
    private SetFuelCardBalanceStrategy setFuelCardBalanceStrategy;

    @Mock
    private SetDrugstoreCardBalanceStrategy setDrugstoreCardBalanceStrategy;

    @InjectMocks
    private SetCardBalanceFactory setCardBalanceFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetStrategy_Food() {
        SetCardBalanceStrategy strategy = setCardBalanceFactory.getStrategy(AleloServiceType.FOOD.toString());
        assertEquals(setFoodCardBalanceStrategy, strategy);
    }

    @Test
    void testGetStrategy_Drugstore() {
        SetCardBalanceStrategy strategy = setCardBalanceFactory.getStrategy(AleloServiceType.DRUGSTORE.toString());
        assertEquals(setDrugstoreCardBalanceStrategy, strategy);
    }

    @Test
    void testGetStrategy_Fuel() {
        SetCardBalanceStrategy strategy = setCardBalanceFactory.getStrategy(AleloServiceType.FUEL.toString());
        assertEquals(setFuelCardBalanceStrategy, strategy);
    }

    @Test
    void testGetStrategy_InvalidType() {

        SetCardBalanceStrategyException exception = assertThrows(SetCardBalanceStrategyException.class, () -> {
            setCardBalanceFactory.getStrategy("INVALID");
        });
        assertEquals("Tipo de cartão não encontrado", exception.getMessage());
    }
}
