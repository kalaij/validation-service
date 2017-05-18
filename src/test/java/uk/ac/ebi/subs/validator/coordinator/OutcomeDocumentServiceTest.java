package uk.ac.ebi.subs.validator.coordinator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.validator.data.ValidationOutcome;
import uk.ac.ebi.subs.validator.repository.ValidationOutcomeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationOutcomeRepository.class)
@EnableAutoConfiguration
@SpringBootTest(classes = OutcomeDocumentService.class)
public class OutcomeDocumentServiceTest {

    @Autowired
    ValidationOutcomeRepository repository;

    @Autowired
    OutcomeDocumentService service;

    @Before
    public void setUp() {
        List<ValidationOutcome> validationOutcomeList = generateValidationOutcomes(5);
        repository.insert(validationOutcomeList);
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void getVersionTest() {
        Assert.assertEquals("4.5", service.getVersion("123", "44566"));
    }

    private List<ValidationOutcome> generateValidationOutcomes(int numberOfDocs) {
        List<ValidationOutcome> validationOutcomes = new ArrayList<>();
        ValidationOutcome validationOutcome;

        int i = 0;
        while (i < numberOfDocs) {
            validationOutcome = new ValidationOutcome();
            validationOutcome.setUuid(UUID.randomUUID().toString());
            validationOutcome.setVersion(Double.valueOf(i + 0.4).toString());
            validationOutcome.setSubmissionId("123");
            validationOutcome.setEntityUuid("44566");
            validationOutcomes.add(validationOutcome);

            i++;
        }
        return validationOutcomes;
    }
}
