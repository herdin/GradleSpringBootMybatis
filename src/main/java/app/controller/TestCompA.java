package app.controller;

import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class TestCompA {
    public TestCompA() {
        System.out.println(this.getClass().getSimpleName());
    }
}
