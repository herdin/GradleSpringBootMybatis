package app.controller;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class TestCompC {
    public TestCompC() {
        System.out.println(this.getClass().getSimpleName());
    }
}
