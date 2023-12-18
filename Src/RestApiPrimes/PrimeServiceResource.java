package com.example;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("primes")
public class PrimeServiceResource {

    private static final String PRIME_FILE_PATH = PrimeGeneration.PRIME_FILE_PATH;
    private static final String NON_PRIME_FILE_PATH = PrimeGeneration.NON_PRIME_FILE_PATH;

    @GET
    @Path("check/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkNumber(@PathParam("number") int number, @QueryParam("isPrime") boolean isPrime) {
        if (readNumbersFromFile(PRIME_FILE_PATH).contains(number)) {
            return Response.ok().entity(new Result("Prime")).build();
        } else if (readNumbersFromFile(NON_PRIME_FILE_PATH).contains(number)) {
            return Response.ok().entity(new Result("Non-Prime")).build();
        } else {
            return Response.ok().entity(new Result("Number not found")).build();
        }
    }

    @POST
    @Path("store/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeNumber(@PathParam("number") int number, @QueryParam("isPrime") boolean isPrime) {
        List<Integer> primeNumbers = readNumbersFromFile(PRIME_FILE_PATH);
        List<Integer> nonPrimeNumbers = readNumbersFromFile(NON_PRIME_FILE_PATH);

        if (primeNumbers.contains(number)) {
            return Response.ok().entity(new Result("Number already exists and is a prime")).build();
        } else if (nonPrimeNumbers.contains(number)) {
            return Response.ok().entity(new Result("Number already exists and is not a prime")).build();
        } else{
            if (isPrime) {
                primeNumbers.add(number);
                writeNumbersToFile(PRIME_FILE_PATH, primeNumbers);
                return Response.ok().entity(new Result("Stored as Prime")).build();
            } else {
                nonPrimeNumbers.add(number);
                writeNumbersToFile(NON_PRIME_FILE_PATH, nonPrimeNumbers);
                return Response.ok().entity(new Result("Stored as Non-Prime")).build();
            }
        }
    }

    private List<Integer> readNumbersFromFile(String filePath) {
        List<Integer> numbers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    int value = Integer.parseInt(line);
                    numbers.add(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numbers;
    }

    private void writeNumbersToFile(String filePath, List<Integer> numbers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int number : numbers) {
                writer.write(Integer.toString(number));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /* 
    @GET
    @Path("myresource")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    */


}