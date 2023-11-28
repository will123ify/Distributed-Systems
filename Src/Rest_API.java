package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("RestAPI/")
public class Rest_API {
    
    static String path = "C:\\Users\\Jimmy\\VSC\\simple-service\\primeNumbers.txt";
    // Inserting the first 10000 prime numbers into a .txt document
    public static void main (String[] args) {
        createPrimeList();

    }

    public static boolean isPrime(int num) {
        if (num < 2) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
    
    public static void createPrimeList() {
        int size = 10000;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for (int num = 2; num <= size; num++) {
                if (isPrime(num)) {
                    writer.write(Integer.toString(num));
                    writer.newLine();
                    
                }
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    @Path("isPrime/{number}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public boolean getPrime(@PathParam("number") int number) 
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    int value = Integer.parseInt(line);
                    if (value == number) {
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception io) {
            io.printStackTrace();
        }
        return false;
    }
    

    @Path("isPrime/{number}")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String insertPrime(@PathParam ("number") int num) {
        try 
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path,true));
            writer.write(num);
            writer.newLine();
            writer.close();
            return "Prime successfully added to the list";
        }
        catch(Exception e) 
        {  
            e.printStackTrace();
            return "Unable to insert prime to list";
        }
    }
 

}
