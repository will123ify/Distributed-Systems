package com.example;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("primes")
public class PrimeServiceResource {

    private static List<Integer> primeNumbers = new ArrayList<>();
    private static List<Integer> nonPrimeNumbers = new ArrayList<>();

    @GET
    @Path("check/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkNumber(@PathParam("number") int number) {
        if (primeNumbers.contains(number)) {
            return Response.ok().entity(new Result("Prime")).build();
        } else if (nonPrimeNumbers.contains(number)) {
            return Response.ok().entity(new Result("Non-Prime")).build();
        } else {
            // The number doesn't exist in primeNumbers
            return Response.status(Response.Status.NOT_FOUND).entity(new Result("Number not found")).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrimes() {
        return Response.ok().entity(primeNumbers).build();
    }

    @POST
    @Path("store/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeNumber(@PathParam("number") int number) {
        if (primeNumbers.contains(number)) {
            return Response.ok().entity(new Result("Number already exists and is a prime")).build();
        } else if (nonPrimeNumbers.contains(number)) {
            return Response.ok().entity(new Result("Number already exists and is not a prime")).build();
        } else {
            if (isPrime(number)) {
                primeNumbers.add(number);
                return Response.ok().entity(new Result("Stored as Prime")).build();
            } else {
                nonPrimeNumbers.add(number);
                return Response.ok().entity(new Result("Stored as Non-Prime")).build();
            }
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

    public static void generatePrimes() {
        primeNumbers.clear(); // Clear existing primes 
        for (int i = 2; i <= 100; i++) {
            if (isPrime(i)) {
                primeNumbers.add(i);
            }
            else {
                nonPrimeNumbers.add(i);
            }
        }
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