package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ClientRegistration {
    private static List<Registration> registrationList = new ArrayList<Registration>();

    private static HttpServer server;
    private static WebTarget target;

    public static void main(String[] args) {
        register();
    }

    public static void register() {
        List<Integer> numbers = new ArrayList<Integer>();
        List<LocalDateTime> timeSlots = new ArrayList<LocalDateTime>();
        List<Combo> combos = new ArrayList<Combo>();
        boolean entries = false;
        boolean timeflag = false;
        boolean numberflag = false;
        boolean finishedFlag = false;
        boolean moreEntries = false;
        Scanner scanner = new Scanner(System.in);
        LocalDateTime datetime = null;

        int number = 0;
        String email = "";
        boolean historical = false;

        System.out.println("Welcome to Jimmys LotteryService");
        System.out.println("'/register' to register or '/retrieve' to see previous winners");
        String input = scanner.nextLine();
        if (input.equals("/retrieve")) {
            while (!historical) {
                System.out.println(
                        "Enter the start date (dd/MM/yy HH:ss) of the period you want to retrieve the historical data from");
                String from = scanner.nextLine();
                System.out.println(
                        "Enter the end date (dd/MM/yy HH:ss) of the period you want to retrieve the historical data from");
                String to = scanner.nextLine();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
                    LocalDateTime periodFrom = LocalDateTime.parse(from, formatter);
                    LocalDateTime periodTo = LocalDateTime.parse(to, formatter);
                    if (periodTo.isBefore(periodFrom)) {
                        System.out.println("The start date must be before the end date. Try again");
                    } else {
                        getHistorical(periodFrom, periodTo);
                        historical = true;
                    }

                } catch (DateTimeParseException e) {
                    System.out.println("Invalid time format. Try again");
                }
            }
        } else if (input.equals("/register")) {

            System.out.println(
                    "Welcome!");
            while (!entries) {
                entries = false;
                timeflag = false;
                numberflag = false;
                finishedFlag = false;
                moreEntries = false;

                System.out.println("Please specify date (dd/MM/yy) and which time slot you want to register: (HH:mm)");
                while (!timeflag) {
                    try {
                        String time = scanner.nextLine();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
                        datetime = LocalDateTime.parse(time, formatter);
                        if (LocalDateTime.now().isAfter(datetime) || datetime.getMinute() != 00) {
                            System.out.println("The specified time slot is not available. Try again");
                        } else {
                            timeflag = true;
                            
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid time format. Try again");
                    }

                }
                while (!numberflag) {
                    System.out.println("Enter your lucky number!");
                    number = scanner.nextInt();
                    Combo combo = new Combo(number, datetime);
                    if (number > 255 || number < 0) {
                        System.out.println("Number must be from 0 to 255");
                    } else if (existsInList(combos, combo)) {
                        System.out.println("Number already registered. Please choose another one");
                    } else {
                        combos.add(combo);
                        numberflag = true;
                    }
                    scanner.nextLine();
                }

                while (!moreEntries) {
                    System.out.println("Do you want to register more entries? (y/n)");
                    String resp = scanner.nextLine();
                    Boolean bool = resp.equals("n") ? true : false;
                    if (bool) {
                        entries = true;
                        moreEntries = true;
                    } else if (!bool) {
                        moreEntries = true;
                    } else {
                        System.out.println("Invalid input. Try again");
                    }
                }
            }

            while (!finishedFlag) {
                System.out.println("Enter your email for results.");
                email = scanner.nextLine();
                if (!isValidEmailAddress(email)) {
                    System.out.println("Invalid mailaddress format");
                } else {
                    finishedFlag = true;
                }
            }
            
            registrationList = convertCombo(combos, email);
            sendListAsJSON(registrationList);
        } else {
            System.out.println("Invalid input. Try again");
        }
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static void sendListAsJSON(List<Registration> registrationList) {
        // create the client
        ObjectMapper mapper = new ObjectMapper();
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(mapper); // Jackson
        SimpleModule modul = new SimpleModule();
        modul.addSerializer(LocalDateTime.class, new CustomDateSerializer());
        mapper.registerModule(modul);
        Client c = ClientBuilder.newBuilder().register(jsonProvider).build();
        String jsonRegistrations = null;

        String endpointURL = "http://localhost:8080/Lottery/registerList";
        try {
            jsonRegistrations = mapper.writeValueAsString(registrationList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Response response = c.target(endpointURL)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .put(Entity.entity(jsonRegistrations, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            String successMessage = response.readEntity(String.class);
            System.out.println(successMessage);
        } else {
            System.out.println("Failed to send registrations. HTTP error code: " + response.getStatus());
        }

        // Close the client
        c.close();
    }

    public static void getHistorical(LocalDateTime from, LocalDateTime to) {
        // create the client
        String endpointURL = "http://localhost:8080/Lottery/historicalWinners/";
        ObjectMapper mapper = new ObjectMapper();
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(mapper); // Jackson
        SimpleModule modul = new SimpleModule();
        modul.addSerializer(LocalDateTime.class, new CustomDateSerializer());
        mapper.registerModule(modul);
        Client c = ClientBuilder.newBuilder().register(jsonProvider).build();
        List<SummarizeWinners> wins = new ArrayList<SummarizeWinners>();
        List<SummarizeWinners> histList = new ArrayList<SummarizeWinners>();

        histList = c.target(endpointURL)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<SummarizeWinners>>() {
                });

        if (!histList.isEmpty()) {
            for (SummarizeWinners reg : histList) {
                if (from.isBefore(reg.getTime()) && to.isAfter(reg.getTime()) || from.isEqual(reg.getTime()) || to.isEqual(reg.getTime())) {
                    wins.add(reg);
                }
            }

            for (SummarizeWinners win : wins) {
                System.out.println("Date & time of roll: " + win.getTime() + "\n" + "Number of winners: "
                        + win.getWinners()
                        + "\n" + "Money pool: " + win.getMoneypool() + "\n" + "Winning number: " + win.getNum() + "\n");
            }
        } else {
            System.out.println("No winners found");
        }
        // Close the client
        c.close();
    }

    public static List<Registration> convertCombo(List<Combo> list, String email) {
        List<Registration> list1 = new ArrayList<Registration>();
        for(Combo combo : list) {
            list1.add(new Registration(combo.getTime(), combo.getNum(), email));
        }
        return list1;
    }
    public static boolean existsInList(List<Combo> list, Combo combo) {
        for(Combo c : list) {
            if(c.getNum() == combo.getNum() && c.getTime().equals(combo.getTime())) {
                return true;
            }
        }
        return false;
    }
       
}