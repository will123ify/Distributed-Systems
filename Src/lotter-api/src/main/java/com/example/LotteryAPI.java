package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("Lottery/")
public class LotteryAPI {
    private String historical = "C:\\Users\\Jimmy\\VSC\\lotter-api\\Historical.txt";
    private String registration = "C:\\Users\\Jimmy\\VSC\\lotter-api\\Registrations.txt";

    /// TODO: implement register method. Save time,date,name and guess(es). Also
    /// take email for results.
    @Path("registerList")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String register(List<Registration> regi) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(registration, true));
            for (Registration registration : regi) {
                writer.write(
                        registration.getTime().toString() + " " + registration.getNumber() + " "
                                + registration.getEmail() + "\n");
            }
            writer.flush();
            writer.close();
            return "Registration Successful";
        } catch (IOException e) {
            e.printStackTrace();
            return "Registration Failed";
        }
    }

    // Returns the list of registrations
    @Path("registrationList")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Registration> getRegList() {
        List<Registration> regList = new ArrayList<Registration>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(registration));
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                LocalDateTime dateTime = LocalDateTime.parse(parts[0], formatter);
                int value = Integer.parseInt(parts[1]);
                String email = parts[2];
                regList.add(new Registration(dateTime, value, email));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (regList.isEmpty()) {
            return regList = new ArrayList<Registration>();
        } else {
            return regList;
        }
    }

    @Path("historicalWinners")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SummarizeWinners> getHistoricalData() {
        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        List<SummarizeWinners> summary = new ArrayList<SummarizeWinners>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(historical));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                LocalDateTime dateTime = LocalDateTime.parse(parts[0], dateformat);
                int winners = Integer.parseInt(parts[1]);
                int moneypool = Integer.parseInt(parts[2]);
                int num = Integer.parseInt(parts[3]);
                SummarizeWinners summarizeWinners = new SummarizeWinners(dateTime, winners, moneypool, num);
                summary.add(summarizeWinners);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!summary.isEmpty()) {
            return summary;
        }
        return summary;

    }

    @Path("historical")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addHistorical(SummarizeWinners win) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(historical, true));

            writer.write(
                    win.getTime() + " " + win.getWinners() + " " + +win.getMoneypool() + " " + win.getNum() + "\n");
            writer.flush();
            writer.close();
            return "Previous winners added";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error while adding previous winners";
        }
    }

}
