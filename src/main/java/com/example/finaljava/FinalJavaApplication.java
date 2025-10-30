package com.example.finaljava; // <-- move to a proper package

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class}) // <-- exclude R2DBC
public class FinalJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalJavaApplication.class, args);
    }

}
