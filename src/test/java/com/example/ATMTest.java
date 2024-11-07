package com.example;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class ATMTest {

    private Bank mockBank;
    private User mockUser;
    private ATM atm;

    @BeforeEach
    void setUp() {
        mockBank = mock(Bank.class);
        mockUser = mock(User.class);
        atm = new ATM();
        atm.bank = mockBank;
        atm.currentUser = mockUser;
    }

    @Test
    @DisplayName("Test successful card insertion")
    public void testInsertCardAccepted() {
        when(mockBank.getUserById("adam")).thenReturn(mockUser);
        when(mockBank.isCardLocked("adam")).thenReturn(false);

        assertTrue(atm.insertCard("adam"));
        verify(mockBank, times(1)).getUserById("adam");
    }

    @Test
    @DisplayName("Test card insertion failure(locked card)")
    public void testInsertCardFailureLockedCard() {
        when(mockBank.getUserById("adam")).thenReturn(mockUser);
        when(mockBank.isCardLocked("adam")).thenReturn(true);

        assertFalse(atm.insertCard("adam"));
        verify(mockBank, times(1)).isCardLocked("adam");
    }

    @Test
    @DisplayName("Test for the correct pin for successful log in")
    public void testEnterCorrectPin() {
        when(mockUser.getPin()).thenReturn("1267");
        atm.currentUser = mockUser;

        assertTrue(atm.enterPin("1267"));
        verify(mockUser, times(1)).resetFailedAttempts();
    }

    @Test
    @DisplayName("Test for pin failure")
    public void testEnterPinFailure() {
        when(mockUser.getPin()).thenReturn("1267");
        atm.currentUser = mockUser;
        assertFalse(atm.enterPin("1263"));
        verify(mockUser, times(1)).incrementFailedAttempts();
    }

    @Test
    @DisplayName("Test entering incorrect PIN with attempts and card lock after third attempt")
    public void testEnterIncorrectPinWithAttemptsWithLockout() {
        User spyUser = spy(new User("adam", "1253", 1000.0));
        atm.currentUser = spyUser;

        for (int i = 1; i <= 2; i++) {
            assertFalse(atm.enterPin("0000"));
            verify(spyUser, times(i)).incrementFailedAttempts();
            int expectedRemainingAttempts = 3 - i;
            assertEquals(expectedRemainingAttempts, 3 - spyUser.getFailedAttempts(),
                    "Remaining attempts should decrease");
            assertFalse(spyUser.isLocked(), "Card should not be locked yet.");
        }

        assertFalse(atm.enterPin("0000"));
        verify(spyUser, times(3)).incrementFailedAttempts();
        assertTrue(spyUser.isLocked(), "Card should be locked after this");
    }



    @Test
    @DisplayName("Test deposit")
    public void testDeposit() {
        atm.currentUser = mockUser;
        atm.deposit(544);
        verify(mockUser, times(1)).deposit(544);
    }

    @Test
    @DisplayName("Test deposit with negative number")
    public void testDepositNegative() {
        atm.currentUser = mockUser;
        atm.deposit(-544);
        verify(mockUser, never()).deposit(-544);
    }

    @Test
    @DisplayName("Test successful withdrawal")
    public void testWithdrawSuccessfully() {
        when(mockUser.getBalance()).thenReturn(1200.0);
        atm.currentUser = mockUser;
        assertTrue(atm.withdraw(355));
        verify(mockUser, times(1)).withdraw(355);
    }

    @Test
    @DisplayName("Test withdrawal failure not enough balance")
    public void testWithdrawNotEnoughBalance() {
        when(mockUser.getBalance()).thenReturn(30.0);
        atm.currentUser = mockUser;
        assertFalse(atm.withdraw(122.0));
        verify(mockUser, never()).withdraw(122.0);
    }

    @Test
    @DisplayName("Test bank name retrieval using mock static method")
    public void testGetBankName() {
        try (MockedStatic<Bank> mockedBank = mockStatic(Bank.class)) {
            mockedBank.when(Bank::getBankName).thenReturn("MockBank");
            assertEquals("MockBank", Bank.getBankName());
            mockedBank.verify(Bank::getBankName, times(1));
        }
    }}