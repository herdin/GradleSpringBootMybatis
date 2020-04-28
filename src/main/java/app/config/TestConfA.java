package app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

@Configuration
public class TestConfA {
    public TestConfA() {
        System.out.println(this.getClass().getSimpleName());
    }
    @PostConstruct
    public void init() {
        System.out.println("post construct");
    }
}
