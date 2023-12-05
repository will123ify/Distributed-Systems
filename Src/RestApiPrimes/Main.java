package com.example;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Main {
    public static void main(String[] args) {
        PrimeServiceResource.generatePrimes();
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        ResourceConfig config = new ResourceConfig(PrimeServiceResource.class);
        JdkHttpServerFactory.createHttpServer(baseUri, config);
        System.out.println("Server started on " + baseUri);
    }
}
