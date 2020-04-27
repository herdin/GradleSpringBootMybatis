package app.controller;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TestCompB {
    public TestCompB() {
        System.out.println(this.getClass().getSimpleName());
    }
}
