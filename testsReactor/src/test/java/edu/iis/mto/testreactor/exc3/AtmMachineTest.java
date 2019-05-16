package edu.iis.mto.testreactor.exc3;

import static com.sun.javaws.JnlpxArgs.verify;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AtmMachineTest {


    private BankService bankService;
    private CardProviderService cardService;
    private MoneyDepot moneyDepot;
    private AtmMachine atmMachine;
    private AuthenticationToken authenticationToken;
    private Card card;
    private Money money;


    @Before
    public void setup() {
        bankService = mock(BankService.class);
        cardService = mock(CardProviderService.class);
        moneyDepot = mock(MoneyDepot.class);

        atmMachine = new AtmMachine(cardService, bankService, moneyDepot);

        card = Card.builder()
                   .withCardNumber("cardNumber")
                   .withPinNumber(2345)
                   .build();

        authenticationToken = AuthenticationToken.builder()
                                                 .withAuthorizationCode(2345)
                                                 .withUserId("id1")
                                                 .build();
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


        atmMachine.withdraw(money, card);
    }

    @Test(expected = WrongMoneyAmountException.class)
    public void testShouldThrowWrongMoneyAmountExceptionBecausecannotBePayedWithBanknotes() {

        money = Money.builder()
                     .withAmount(1111)
                     .withCurrency(Currency.PL)
                     .build();


        atmMachine.withdraw(money, card);
    }

    @Test(expected = CardAuthorizationException.class)
    public void testShouldThrowCardAuthorizationException() {

        money = Money.builder()
                     .withAmount(1000)
                     .withCurrency(Currency.PL)
                     .build();



        when(cardService.authorize(card)).thenReturn(Optional.empty());

        atmMachine.withdraw(money, card);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testShouldThrowInsufficientFundsException() {
        money = Money.builder()
                     .withAmount(1000)
                     .withCurrency(Currency.PL)
                     .build();



        when(cardService.authorize(card)).thenReturn(Optional.of(authenticationToken));

        atmMachine.withdraw(money, card);
    }

    @Test(expected = MoneyDepotException.class)
    public void testShouldThrowMoneyDepotException() {
        money = Money.builder()
                     .withAmount(10)
                     .withCurrency(Currency.PL)
                     .build();


        when(cardService.authorize(card)).thenReturn(Optional.of(authenticationToken));

        when(bankService.charge(authenticationToken, money)).thenReturn(true);

        Payment payment = atmMachine.withdraw(money, card);
    }


    @Test
    public void testShouldReturnThatMethodChargeWasCalledTwice() {
        money = Money.builder()
                     .withAmount(10)
                     .withCurrency(Currency.PL)
                     .build();





        when(cardService.authorize(card)).thenReturn(Optional.of(authenticationToken));

        when(bankService.charge(authenticationToken, money)).thenReturn(true);

        List<Banknote> banknotes = Banknote.forCurrency(money.getCurrency())
                                           .stream()
                                           .sorted(Collections.reverseOrder())
                                           .collect(Collectors.toList());

        int amount = money.getAmount();
        List<Banknote> paymentBanknotes = new ArrayList<>();
        for (Banknote banknote : banknotes) {
            while (amount >= banknote.getValue()) {
                amount = amount - banknote.getValue();
                paymentBanknotes.add(banknote);
            }
        }

        when(moneyDepot.releaseBanknotes(paymentBanknotes)).thenReturn(true);

        Payment payment = atmMachine.withdraw(money, card);
        Payment payment2 = atmMachine.withdraw(money, card);
        Mockito.verify(bankService, times(2)).charge(authenticationToken, money);
    }

    @Test
    public void testShouldReturnThatMethodReleaseBanknotesWasCalledOnce() {
        money = Money.builder()
                     .withAmount(10)
                     .withCurrency(Currency.PL)
                     .build();





        when(cardService.authorize(card)).thenReturn(Optional.of(authenticationToken));

        when(bankService.charge(authenticationToken, money)).thenReturn(true);

        List<Banknote> banknotes = Banknote.forCurrency(money.getCurrency())
                                           .stream()
                                           .sorted(Collections.reverseOrder())
                                           .collect(Collectors.toList());

        int amount = money.getAmount();
        List<Banknote> paymentBanknotes = new ArrayList<>();
        for (Banknote banknote : banknotes) {
            while (amount >= banknote.getValue()) {
                amount = amount - banknote.getValue();
                paymentBanknotes.add(banknote);
            }
        }

        when(moneyDepot.releaseBanknotes(paymentBanknotes)).thenReturn(true);

        Payment payment = atmMachine.withdraw(money, card);
        Mockito.verify(moneyDepot, times(1)).releaseBanknotes(paymentBanknotes);
    }

    @Test
    public void testShouldReturnThatIsOneBanknot() {
        money = Money.builder()
                     .withAmount(10)
                     .withCurrency(Currency.PL)
                     .build();



        when(cardService.authorize(card)).thenReturn(Optional.of(authenticationToken));

        when(bankService.charge(authenticationToken, money)).thenReturn(true);

        List<Banknote> banknotes = Banknote.forCurrency(money.getCurrency())
                                           .stream()
                                           .sorted(Collections.reverseOrder())
                                           .collect(Collectors.toList());

        int amount = money.getAmount();
        List<Banknote> paymentBanknotes = new ArrayList<>();
        for (Banknote banknote : banknotes) {
            while (amount >= banknote.getValue()) {
                amount = amount - banknote.getValue();
                paymentBanknotes.add(banknote);
            }
        }

        when(moneyDepot.releaseBanknotes(paymentBanknotes)).thenReturn(true);

        Payment payment = atmMachine.withdraw(money, card);
        assertThat(payment.getValue().size(), Matchers.is(1));
    }

    @Test
    public void testShouldReturnThatIstwoBanknots() {
        money = Money.builder()
                     .withAmount(30)
                     .withCurrency(Currency.PL)
                     .build();




        when(cardService.authorize(card)).thenReturn(Optional.of(authenticationToken));

        when(bankService.charge(authenticationToken, money)).thenReturn(true);

        List<Banknote> banknotes = Banknote.forCurrency(money.getCurrency())
                                           .stream()
                                           .sorted(Collections.reverseOrder())
                                           .collect(Collectors.toList());

        int amount = money.getAmount();
        List<Banknote> paymentBanknotes = new ArrayList<>();
        for (Banknote banknote : banknotes) {
            while (amount >= banknote.getValue()) {
                amount = amount - banknote.getValue();
                paymentBanknotes.add(banknote);
            }
        }

        when(moneyDepot.releaseBanknotes(paymentBanknotes)).thenReturn(true);

        Payment payment = atmMachine.withdraw(money, card);
        assertThat(payment.getValue().size(), Matchers.is(2));
    }

}
