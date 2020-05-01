package com.harm.app.controller;

import org.springframework.stereotype.Component;

@Component
public class TestCompC {
    public TestCompC() {
        System.out.println(this.getClass().getSimpleName());
    }
}
