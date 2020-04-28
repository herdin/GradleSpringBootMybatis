package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
//@MapperScan(value = "app.mapper")
public class MainApp {
    public static void main(String[] args) {
//        SpringApplication application = new SpringApplication(MainApp.class);
//        application.run(args);
        try {
            System.out.println(InetAddress.getLocalHost().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
