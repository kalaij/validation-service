package uk.ac.ebi.subs.validator.coordinator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationResultRepository.class)
@EnableAutoConfiguration
@SpringBootTest(classes = ValidationResultService.class)
public class ValidationResultServiceTest {

    @Autowired
    ValidationResultRepository repository;

    @Autowired
    ValidationResultService service;

    @Before
    public void setUp() {
        repository.deleteAll();
        List<ValidationResult> validationOutcomeList = generateValidationOutcomes(5);
        repository.insert(validationOutcomeList);
    }

    @Test
    public void getVersionTest() {
        Assert.assertEquals(5, service.getVersion("123", "44566"));
    }

    private List<ValidationResult> generateValidationOutcomes(int numberOfDocs) {
        List<ValidationResult> validationOutcomes = new ArrayList<>();
        ValidationResult validationOutcome;

        int i = 0;
        while (i < numberOfDocs) {
            validationOutcome = new ValidationResult();
            validationOutcome.setUuid(UUID.randomUUID().toString());
            validationOutcome.setVersion(i);
            validationOutcome.setSubmissionId("123");
            validationOutcome.setEntityUuid("44566");
            validationOutcomes.add(validationOutcome);

            i++;
        }
        return validationOutcomes;
    }
}
