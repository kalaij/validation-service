package uk.ac.ebi.subs.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("uk.ac.ebi.subs.validator")
public class CoordinatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoordinatorApplication.class, args);
    }
}
