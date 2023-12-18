package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestClient {

    public static void main(String[] args) {
        // Base URL of the server
        String baseUrl = "http://localhost:8080/";
        boolean isPrime;

        // Run the client and keep taking user input until "exit" is entered
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String userInput;
            do {
                System.out.print("Enter command (e.g., '(GET)check 7', '(POST)store 11', 'exit'): ");
                userInput = reader.readLine();

                // Process user input and generate requests
                if (userInput.startsWith("check")) {
                    int number = extractNumber(userInput);
                    isPrime = isPrime(number);
                    //System.out.println("The number " + number + " is " + (isPrime ? "prime" : "not prime"));
                    sendGetRequest(baseUrl + "primes/check/" + number);
                } else if (userInput.startsWith("store")) {
                    int number = extractNumber(userInput);
                    isPrime = isPrime(number);
                    //System.out.println("The number " + number + " is " + (isPrime ? "prime" : "not prime"));
                    sendPostRequest(baseUrl + "primes/store/" + number);
                } else if (!userInput.equalsIgnoreCase("exit")) {
                    System.out.println("Invalid command. Please try again.");
                }
            } while (!userInput.equalsIgnoreCase("exit"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendGetRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("GET Request Response Code: " + responseCode);

            // Read the response content
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("GET Request Response: " + response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendPostRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("POST Request Response Code: " + responseCode);

            // Read the response content
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("POST Request Response: " + response.toString());
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int extractNumber(String input) {
        // Extract the number from the user input
        String[] parts = input.split(" ");
        if (parts.length > 1) {
            try {
                return Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return -1; // Return -1 if the number cannot be extracted
    }

    public static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
