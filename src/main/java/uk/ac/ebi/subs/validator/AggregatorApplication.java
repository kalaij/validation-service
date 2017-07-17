package uk.ac.ebi.subs.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;

/**
 * Entry point of the Validator Aggregator application.
 *
 * Created by karoly on 08/05/2017.
 */
@SpringBootApplication
@ComponentScan("uk.ac.ebi.subs.validator")
public class AggregatorApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AggregatorApplication.class);
        ApplicationPidFileWriter applicationPidFileWriter = new ApplicationPidFileWriter();
        springApplication.addListeners(applicationPidFileWriter);
        springApplication.run(args);
    }

}
