package com.bank.demo.utils;

public class Account {
    String number;
    String pin;
    int balance;

    public Account(String number, String pin, int balance) {
        this.number = number;
        this.pin = pin;
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }

}
