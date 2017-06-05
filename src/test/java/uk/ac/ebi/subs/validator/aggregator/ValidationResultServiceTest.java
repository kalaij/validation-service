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
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationResultRepository.class)
@EnableAutoConfiguration
@SpringBootTest(classes = ValidationResultService.class)
public class ValidationResultServiceTest {

    String oldDocUUID;
    String newDocUUID;

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
     * Check that current version is the most up to date.
     */
    @Test
    public void isLatestVersionTest1() {
        Assert.assertTrue(service.isLatestVersion("123", "44566", 3));
    }

    /**
     *  Check that current version is obsolete.
     */
    @Test
    public void isLatestVersionTest2() {
        Assert.assertTrue(!service.isLatestVersion("123", "44566", 1));
    }

    /**
     * Update validation result document successfully.
     */
    @Test
    public void updateValidationResultTest1() {
        SingleValidationResult singleValidationResult = generateEntityValidationResult(2, newDocUUID);

        Assert.assertTrue(service.updateValidationResult(singleValidationResult));
    }

    /**
     * Ignore entity validation result that refers to an older version and skip the validation result document update.
     */
    @Test
    public void updateValidationResultTest2() {
        SingleValidationResult singleValidationResult = generateEntityValidationResult(1, oldDocUUID);

        Assert.assertTrue(!service.updateValidationResult(singleValidationResult));
    }

    private List<ValidationResult> generateValidationResults() {
        List<ValidationResult> validationResults = new ArrayList<>();

        Map<Archive, Boolean> archiveBooleanMap = new HashMap<>();
        archiveBooleanMap.put(Archive.BioSamples, false);
        archiveBooleanMap.put(Archive.ArrayExpress, false);
        archiveBooleanMap.put(Archive.Ena, false);

        // First
        ValidationResult validationResult1 = new ValidationResult();
        validationResult1.setUuid(UUID.randomUUID().toString());
        oldDocUUID = validationResult1.getUuid();
        validationResult1.setExpectedResults(archiveBooleanMap);
        validationResult1.setVersion(1);
        validationResult1.setSubmissionId("123");
        validationResult1.setEntityUuid("44566");

        validationResults.add(validationResult1);

        // Second
        ValidationResult validationResult2 = new ValidationResult();
        validationResult2.setUuid(UUID.randomUUID().toString());
        newDocUUID = validationResult2.getUuid();
        validationResult2.setExpectedResults(archiveBooleanMap);
        validationResult2.setVersion(2);
        validationResult2.setSubmissionId("123");
        validationResult2.setEntityUuid("44566");

        validationResults.add(validationResult2);

        return validationResults;
    }

    private SingleValidationResult generateEntityValidationResult(int version, String docUUID) {
        SingleValidationResult singleValidationResult = new SingleValidationResult();
        singleValidationResult.setValidationResultUUID(docUUID);
        singleValidationResult.setEntityUuid("44566");
        singleValidationResult.setUuid(UUID.randomUUID().toString());
        singleValidationResult.setArchive(Archive.BioSamples);
        singleValidationResult.setVersion(version);
        return singleValidationResult;
    }

}
