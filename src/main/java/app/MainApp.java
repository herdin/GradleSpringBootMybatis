package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan(value = "app.mapper")
public class MainApp {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MainApp.class);
//        application.addListeners(new VaultConfigListener());
//        application.addInitializers(new VaultApplicationContextInitializer());
//        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}
