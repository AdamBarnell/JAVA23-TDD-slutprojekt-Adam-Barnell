
package com.example;

import java.util.Scanner;

public class ATM {

    Bank bank;
    User currentUser;

    public ATM() {
        this.bank = new Bank();
    }

    public boolean insertCard(String userId) {
        User user = bank.getUserById(userId);
        if (user == null) {
            System.out.println("Insert a valid card");
            return false;
        } else if ( bank.isCardLocked(userId)) {
            System.out.println("Locked card");
            return false;
        }
        this.currentUser = user;
        System.out.println("Card accepted");
        return true;
    }

    public boolean enterPin(String pin) {

        if (currentUser.getPin().equals(pin)) {
            currentUser.resetFailedAttempts();
            System.out.println("PIN accepted.");
            return true;
        } else {
            currentUser.incrementFailedAttempts();
            int remainingAttempts = 3 - currentUser.getFailedAttempts();
            System.out.println("Incorrect PIN! You have " + remainingAttempts + " attempts left.");

            if (remainingAttempts <= 0) {
                currentUser.lockCard();
                System.out.println("Card locked (too many failed attempts). Please try another card.");
            }
        } return false;
    }



    public double checkBalance() {
        System.out.println("Current balance: " + currentUser.getBalance());
        return currentUser.getBalance();
    }

    public void deposit(double amount) {
        if (amount <= 0){
            System.out.println("Amount can't be negative");
        } else {
            currentUser.deposit(amount);
            System.out.println("Deposited " + amount + " to " + currentUser.getBalance());

        }

    }

    public boolean withdraw(double amount) {
        if (currentUser.getBalance() >= amount) {
            currentUser.withdraw(amount);
            System.out.println("Withdrew " + amount + ". New balance: " + currentUser.getBalance());
            return true;
        } else {
            System.out.println("Not enough money");
            return false;
        }
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the ATM!");
        while (true) {
            System.out.print("\nEnter your card ID: ");
            String cardId = scanner.nextLine();
            if (!atm.insertCard(cardId)) {
                continue;
            }

            System.out.print("Enter your PIN: ");
            String pin = scanner.nextLine();
            if (!atm.enterPin(pin)) {
                System.out.println("Invalid PIN. Try again.");
                continue;
            }

            boolean exit = false;
            while (!exit) {
                System.out.println("\nChoose an option:");
                System.out.println("1. Check Balance");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Exit");
                System.out.print("Enter option: ");

                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        atm.checkBalance();
                        break;
                    case 2:
                        System.out.print("Enter deposit amount: ");
                        double depositAmount = scanner.nextDouble();
                        atm.deposit(depositAmount);
                        break;
                    case 3:
                        System.out.print("Enter withdrawal amount: ");
                        double withdrawAmount = scanner.nextDouble();
                        atm.withdraw(withdrawAmount);
                        break;
                    case 4:
                        System.out.println("Thank you for using the ATM. Goodbye!");
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
            atm.currentUser = null;
        }
    }
}
