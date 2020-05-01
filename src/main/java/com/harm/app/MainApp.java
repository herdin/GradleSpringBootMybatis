package com.harm.app;

import com.harm.app.config.VaultConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

import java.net.URISyntaxException;

@SpringBootApplication
@EnableCircuitBreaker
/**
 * http://localhost:8090/hystrix
 * http://localhost:8090/actuator/hystrix.stream
 */
@EnableHystrixDashboard
public class MainApp {
    static {
        try {
            new VaultConfiguration().init();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(18);
        }
    }
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MainApp.class);
        application.run(args);
    }
}
