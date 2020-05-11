package com.harm.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableCircuitBreaker
/**
 * http://localhost:8090/hystrix
 * http://localhost:8090/actuator/hystrix.stream
 */
@EnableHystrixDashboard
public class MainApp {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MainApp.class);
        application.run(args);
    }
}
