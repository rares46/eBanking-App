package org.poo.cb;

public class Account {
    final private String currency;
    private double balance;

    public Account(String currency) {
        this.currency = currency;
        this.balance = 0.00;
    }

    public String getCurrency() {
        return currency;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
