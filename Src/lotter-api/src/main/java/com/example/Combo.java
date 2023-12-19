package com.example;

import java.time.LocalDateTime;

public class Combo {
    int num;
    LocalDateTime time;

    public Combo(int num, LocalDateTime time) {
        this.num = num;
        this.time = time;
    }

    public int getNum() {
        return num;
    }

    public LocalDateTime getTime() {
        return time;
    }

    }
