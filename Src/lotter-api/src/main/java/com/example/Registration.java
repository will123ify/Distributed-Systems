package com.example;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Registration {
    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDateTime time;
    private int number;
    private String email;

    public Registration() {


    }
    public Registration(LocalDateTime time, int number, String email) {
        this.email = email;
        this.time = time;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getEmail() {
        return email;
    }

}
