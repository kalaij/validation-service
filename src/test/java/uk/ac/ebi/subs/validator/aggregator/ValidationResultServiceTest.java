package uk.ac.ebi.subs.validator.aggregator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationResultRepository.class)
@EnableAutoConfiguration
@SpringBootTest(classes = ValidationResultService.class)
public class ValidationResultServiceTest {

    String exampleDoc1;
    String exampleDoc2;

    @Autowired
    ValidationResultRepository repository;

    @Autowired
    ValidationResultService service;

    @Before
    public void setUp() {
        repository.deleteAll();
        List<ValidationResult> validationResults = generateValidationResults();
        repository.insert(validationResults);
    }

    /**
     * Update validation result document successfully.
     */
    @Test
    public void updateValidationResultTest1() {
        SingleValidationResultsEnvelope singleValidationResultsEnvelope = new SingleValidationResultsEnvelope(
                Arrays.asList(generateSingleValidationResult(1, exampleDoc1)),
                1,
                exampleDoc1,
                ValidationAuthor.Biosamples);

        boolean success = service.updateValidationResult(singleValidationResultsEnvelope);

        Assert.assertTrue(success);
    }

    /**
     * Ignore entity validation result that refers to an older version and skip the validation result document update.
     */
    @Test
    public void updateValidationResultTest2() {

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = new SingleValidationResultsEnvelope(
                Arrays.asList(generateSingleValidationResult(1, exampleDoc2)),
                1,
                exampleDoc2,
                ValidationAuthor.Biosamples);

        Assert.assertTrue(!service.updateValidationResult(singleValidationResultsEnvelope));
    }

    private List<ValidationResult> generateValidationResults() {
        List<ValidationResult> validationResults = new ArrayList<>();

        Map<ValidationAuthor, List<SingleValidationResult>> validationAuthorListMap = new HashMap<>();
        validationAuthorListMap.put(ValidationAuthor.Taxonomy, new ArrayList<>());
        validationAuthorListMap.put(ValidationAuthor.Biosamples, new ArrayList<>());

        // First
        ValidationResult validationResult1 = new ValidationResult();
        validationResult1.setUuid(UUID.randomUUID().toString());
        exampleDoc1 = validationResult1.getUuid();
        validationResult1.setExpectedResults(validationAuthorListMap);
        validationResult1.setVersion(1);
        validationResult1.setSubmissionId("123");
        validationResult1.setEntityUuid("44566");

        validationResults.add(validationResult1);

        // Second
        ValidationResult validationResult2 = new ValidationResult();
        validationResult2.setUuid(UUID.randomUUID().toString());
        exampleDoc2 = validationResult2.getUuid();
        validationResult2.setExpectedResults(validationAuthorListMap);
        validationResult2.setVersion(2);
        validationResult2.setSubmissionId("123");
        validationResult2.setEntityUuid("66977");

        validationResults.add(validationResult2);

        return validationResults;
    }

    private SingleValidationResult generateSingleValidationResult(int version, String docUUID) {
        SingleValidationResult singleValidationResult = new SingleValidationResult();
        singleValidationResult.setValidationResultUUID(docUUID);
        singleValidationResult.setEntityUuid("44566");
        singleValidationResult.setUuid(UUID.randomUUID().toString());
        singleValidationResult.setValidationAuthor(ValidationAuthor.Biosamples);
        singleValidationResult.setVersion(version);
        return singleValidationResult;
    }

}
