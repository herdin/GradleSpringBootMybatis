package com.harm.app.controller;

import org.springframework.stereotype.Component;

@Component
public class TestCompA {
    public TestCompA() {
        System.out.println(this.getClass().getSimpleName());
    }
}
