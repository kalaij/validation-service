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
        List<ValidationResult> validationResults = generateValidationResults(5);
        repository.insert(validationResults);
    }

    @Test
    public void getVersionTest() {
        Assert.assertEquals(5, service.getVersion("123", "44566"));
    }

    private List<ValidationResult> generateValidationResults(int numberOfDocs) {
        List<ValidationResult> validationResults = new ArrayList<>();
        ValidationResult validationResult;

        int i = 0;
        while (i < numberOfDocs) {
            validationResult = new ValidationResult();
            validationResult.setUuid(UUID.randomUUID().toString());
            validationResult.setVersion(i);
            validationResult.setSubmissionId("123");
            validationResult.setEntityUuid("44566");
            validationResults.add(validationResult);

            i++;
        }
        return validationResults;
    }
}
