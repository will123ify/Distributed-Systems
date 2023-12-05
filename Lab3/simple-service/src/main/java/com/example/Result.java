package com.example;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Result {
    private String result;

    public Result() {
    }

    public Result(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}