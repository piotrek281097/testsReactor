package edu.iis.mto.testreactor.exc3;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class AtmMachineTest {


    private BankService bankService;
    private CardProviderService cardProviderService;
    private MoneyDepot moneyDepot;
    private AtmMachine atmMachine;
    private AuthenticationToken authenticationToken;
    private Card card;
    private Money money;

    @Before
    public void setup() {
        bankService = mock(BankService.class);
        cardProviderService = mock(CardProviderService.class);
        moneyDepot = mock(MoneyDepot.class);

        atmMachine = new AtmMachine(cardProviderService, bankService, moneyDepot);

        /*
        authenticationToken = AuthenticationToken.builder()
                                                 .withAuthorizationCode(2345)
                                                 .withUserId("id1")
                                                 .build();

        card = Card.builder()
                   .withCardNumber("cardNumber")
                   .withPinNumber(2345)
                   .build();

        money = Money.builder()
                     .withAmount(1000)
                     .withCurrency(Currency.PL)
                     .build();
                     */
    }

    @Test
    public void itCompiles() {
        assertThat(true, equalTo(true));
    }

    @Test(expected = WrongMoneyAmountException.class)
    public void testShouldThrowWrongMoneyAmountException() {

        money = Money.builder()
                     .withAmount(-1000)
                     .withCurrency(Currency.PL)
                     .build();

        card = Card.builder()
                   .withCardNumber("cardNumber")
                   .withPinNumber(2345)
                   .build();

        atmMachine.withdraw(money, card);
    }

    @Test(expected = WrongMoneyAmountException.class)
    public void testShouldThrowWrongMoneyAmountExceptionBecausecannotBePayedWithBanknotes() {

        money = Money.builder()
                     .withAmount(1111)
                     .withCurrency(Currency.PL)
                     .build();

        card = Card.builder()
                   .withCardNumber("cardNumber")
                   .withPinNumber(2345)
                   .build();

        atmMachine.withdraw(money, card);
    }

}
