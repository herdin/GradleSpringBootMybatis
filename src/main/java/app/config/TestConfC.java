package app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class TestConfC {
    public TestConfC() {
        System.out.println(this.getClass().getSimpleName());
    }
}
