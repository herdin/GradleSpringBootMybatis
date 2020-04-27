package app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

@Configuration
@Primary
public class TestConfB {
    public TestConfB() {
        System.out.println(this.getClass().getSimpleName());
    }
}
