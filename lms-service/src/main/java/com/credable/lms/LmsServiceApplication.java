// lms-service/src/main/java/com/credable/lms/LmsServiceApplication.java
package com.credable.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LmsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LmsServiceApplication.class, args);
    }
}