package com.harm.app.config;

import org.springframework.context.annotation.Configuration;

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
