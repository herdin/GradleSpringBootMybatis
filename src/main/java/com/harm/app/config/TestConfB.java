package com.harm.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Primary
public class TestConfB {
    public TestConfB() {
        System.out.println(this.getClass().getSimpleName());
    }
}
