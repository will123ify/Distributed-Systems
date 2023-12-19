package com.example;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class LotteryServer {
    private static String historical = "C:\\Users\\Jimmy\\VSC\\lotter-api\\Historical.txt";
    private static String registration = "C:\\Users\\Jimmy\\VSC\\lotter-api\\Registrations.txt";
    private static LocalDateTime serverTime = LocalDateTime.of(LocalDateTime.now().getYear(),
            LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(),
            LocalDateTime.now().getHour(),
            00,
            00);
    private static int previousPool = 0;
    // Server class

    // TODO: Implement draw to be used every hour
    public static void startServer() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable server = new Server();
        formatServerTime();
        System.out.println("Server is now running");
        // Specify period and timeunit accordingly
        long period = 10;
        executor.scheduleAtFixedRate(server, 0, period, TimeUnit.SECONDS);
    }

    public static void formatServerTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        String formatted = serverTime.format(formatter);
        serverTime = LocalDateTime.parse(formatted, formatter);
    }

    // Draw a random number
    public static int draw() {
        int num = new Random().nextInt(255);
        return num;
    }

    public static void main(String[] args) {
        clearFiles();
        startServer();
    }

    // Adding winners to the historical sheet
    public static void addHistorical(SummarizeWinners summarize) {
        String endpointURL = "http://localhost:8080/Lottery/historical";
        ObjectMapper mapper = new ObjectMapper();
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(mapper); // Jackson
        SimpleModule modul = new SimpleModule();
        modul.addSerializer(LocalDateTime.class, new CustomDateSerializer());
        mapper.registerModule(modul);
        Client c = ClientBuilder.newBuilder().register(jsonProvider).build();
        String jsonRegistrations = null;

        try {
            jsonRegistrations = mapper.writeValueAsString(summarize);
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
            System.out.println("Failed to add previous winners. HTTP error code: " + response.getStatus());
        }

        // Close the client
        c.close();

    }

    // For the server to constantly run
    private static class Server implements Runnable {
        @Override
        public void run() {
            serverTime = serverTime.plusHours(1);
            drawWinnerList(getActivePool());

        }
    }

    // Retrieve all the registered users
    public static List<Registration> getActivePool() {
        List<Registration> drawingPool = new ArrayList<Registration>();
        List<Registration> list = getRegistrations();

        if (!list.equals(null) && !list.isEmpty()) {
            for (Registration user : list) {
                if (user.getTime().equals(serverTime)) {
                    drawingPool.add(user);
                }
            }
            return drawingPool;
        }
        return drawingPool;

    }

    
    public static List<Registration> drawWinnerList(List<Registration> registeredUsers) {
        List<Registration> winners = new ArrayList<Registration>();
        MoneyPool moneyPool = new MoneyPool();
        int num = draw();
        if (!registeredUsers.isEmpty() && registeredUsers != null) {
            for (Registration user : registeredUsers) {
                moneyPool.add();
            }
        }

        System.out.println("Current time: " + serverTime.getHour() + ":" + serverTime.getMinute() + "\n"
                + "Date: " + serverTime.getDayOfMonth() + "/" + serverTime.getMonthValue() + "/" + serverTime.getYear()
                + "\n"
                + "Lottery is about to start. Current money pool is: " + moneyPool.getCurrentMoneyPool()
                + " SEK. " + "\n" +
                "Current bonus: " + previousPool + " SEK.");
        System.out.println("Winning number is: " + num);
        for (Registration user : registeredUsers) {
            if (user.getNumber() == num) {
                winners.add(user);
            }
        }

        if (winners.isEmpty()) {
            previousPool += moneyPool.getCurrentMoneyPool();
            System.out.println("No Winners! Bonus for next drawing is : " + previousPool +
                    " SEK. Money will be transferred to next drawing. Good luck next time!");
            moneyPool.emptyMoneyPool();
            return null;
        } else {
            moneyPool.add(previousPool);
            System.out.println("Amount of winners: " + winners.size() + "\nEach winner won: "
                    + moneyPool.getCurrentMoneyPool() / winners.size() + " SEK. Congratulations!!" + "\n");
            SummarizeWinners summarizeWinners = new SummarizeWinners(serverTime, winners.size(),
                    moneyPool.getCurrentMoneyPool(), num);
            addHistorical(summarizeWinners);
            notifyWinners(winners);
            moneyPool.emptyMoneyPool();
            previousPool = 0;

            return winners;
        }
    }

    // Retrieve registrations from the database
    public static List<Registration> getRegistrations() {
        String endpointURL = "http://localhost:8080/Lottery/registrationList";
        Client c = ClientBuilder.newClient();
        List<Registration> regList = c.target(endpointURL)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Registration>>() {
                });
        sortList(regList);
        regList = regList != null ? regList : new ArrayList<Registration>();
        return regList;
    }

    // Sort the lists
    public static void sortList(List<Registration> list) {
        list.sort(new Comparator<Registration>() {
            @Override
            public int compare(Registration o1, Registration o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
    }

    public static void notifyWinners(List<Registration> winners) {
        for (Registration winner : winners) {
            // Send out email to winner.getEmail();
        }
    }

    public static void clearFiles()
    {
        try {
            FileWriter fw = new FileWriter(historical, false);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
            fw.close();
            FileWriter fw1 = new FileWriter(registration, false);
            PrintWriter pw1 = new PrintWriter(fw, false);
            pw1.flush();
            pw1.close();
            fw1.close();
            
        } catch (Exception exception) {
            System.out.println("Failed to clean old data");
        }
    }
}