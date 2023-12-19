package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PrimeGeneration {
    
    public static final String PRIME_FILE_PATH = "prime_numbers.txt";
    public static final String NON_PRIME_FILE_PATH = "non_prime_numbers.txt";
    public static int NUMBER_OF_PRIMES = 100;

    // Other methods...

    public static void generatePrimes() {
        List<String> primeLines = new ArrayList<>();
        List<String> nonPrimeLines = new ArrayList<>();

        for (int i = 2; i <= NUMBER_OF_PRIMES; i++) {
            if (RestClient.isPrime(i)) {
                primeLines.add(Integer.toString(i));
            } else {
                nonPrimeLines.add(Integer.toString(i));
            }
        }

        // Write prime numbers to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRIME_FILE_PATH))) {
            for (String line : primeLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write non-prime numbers to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NON_PRIME_FILE_PATH))) {
            for (String line : nonPrimeLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Other methods...
}
