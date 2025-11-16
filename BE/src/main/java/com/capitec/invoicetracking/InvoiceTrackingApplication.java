package com.capitec.invoicetracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class InvoiceTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvoiceTrackingApplication.class, args);
        log.info("Application running...");
    }

}
