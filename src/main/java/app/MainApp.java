package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableCircuitBreaker
public class MainApp {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MainApp.class);
        application.run(args);
    }
}
