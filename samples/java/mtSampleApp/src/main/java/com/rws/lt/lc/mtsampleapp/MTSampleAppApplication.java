package com.rws.lt.lc.mtsampleapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.rws.lt.lc.mtsampleapp", "com.rws.lt.lc.extensibility.security"})
public class MTSampleAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MTSampleAppApplication.class, args);
    }

}
