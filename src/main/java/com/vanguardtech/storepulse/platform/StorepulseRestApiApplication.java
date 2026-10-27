package com.vanguardtech.storepulse.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the StorePulse REST API.
 * the application is monolith organized by Bounded Context: every sub-package of this one.
 * (iam, propertymanagement, storemanagement, etc.) owns its own domain, application,
 * infrastructure and presentation layers, and {@code shared} hold the Shared Kernel.
 */
@SpringBootApplication
public class StorepulseRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorepulseRestApiApplication.class, args);
    }

}
