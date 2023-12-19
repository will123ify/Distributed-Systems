package com.example;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SummarizeWinners {
    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDateTime time;
    private int winners;
    private int moneypool;
    private int num;

    
    public SummarizeWinners() {
        
    }

    public SummarizeWinners(LocalDateTime time, int winners, int moneypool, int num) {
        this.time = time;
        this.winners = winners;
        this.moneypool = moneypool;
        this.num = num;
    }

    public int getWinners() {
        return winners;
    }

    public int getMoneypool() {
        return moneypool;
    }

    public int getNum() {
        return num;
    }

    public LocalDateTime getTime() {
        return time;
    }

}