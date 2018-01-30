package uk.ac.ebi.subs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;

/**
 * Entry point of the Validation Service application.
 *
 * Created by karoly on 18/10/2017.
 */
@SpringBootApplication
public class ValidationServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ValidationServiceApplication.class);
        ApplicationPidFileWriter applicationPidFileWriter = new ApplicationPidFileWriter();
        springApplication.addListeners(applicationPidFileWriter);
        springApplication.run(args);
    }

}
