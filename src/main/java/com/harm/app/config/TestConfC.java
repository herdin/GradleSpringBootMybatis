package com.harm.app.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfC {
    public TestConfC() {
        System.out.println(this.getClass().getSimpleName());
    }
}
