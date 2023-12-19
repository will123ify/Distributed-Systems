package com.example;

public class MoneyPool {
    private int moneyPool = 0;

    public void add() {
        moneyPool += 100;
    }
    public void add(int amount) {
        moneyPool += amount;
    }

    public int getCurrentMoneyPool() {
        return moneyPool;
    }

    public void emptyMoneyPool() {
        moneyPool = 0;
    }
}
