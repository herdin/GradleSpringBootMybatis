package app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

@Configuration
public class TestConfA {
    public TestConfA() {
        System.out.println(this.getClass().getSimpleName());
    }
}
