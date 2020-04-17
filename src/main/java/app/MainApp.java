package app;

import app.runner.VaultApplicationContextInitializer;
import com.sun.org.apache.bcel.internal.generic.ATHROW;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Optional;

@SpringBootApplication
//@MapperScan(value = "app.mapper")
public class MainApp {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MainApp.class);
        application.addInitializers(new VaultApplicationContextInitializer());
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}
