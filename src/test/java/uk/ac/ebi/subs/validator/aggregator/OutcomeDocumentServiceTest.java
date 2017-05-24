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
import uk.ac.ebi.subs.validator.data.EntityValidationOutcome;
import uk.ac.ebi.subs.validator.data.ValidationOutcome;
import uk.ac.ebi.subs.validator.repository.ValidationOutcomeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationOutcomeRepository.class)
@EnableAutoConfiguration
@SpringBootTest(classes = OutcomeDocumentService.class)
public class OutcomeDocumentServiceTest {

    String oldDocUUID;
    String newDocUUID;

    @Autowired
    ValidationOutcomeRepository repository;

    @Autowired
    OutcomeDocumentService service;

    @Before
    public void setUp() {
        repository.deleteAll();
        List<ValidationOutcome> validationOutcomeList = generateValidationOutcomes();
        repository.insert(validationOutcomeList);
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
     * Update validation outcome document successfully.
     */
    @Test
    public void updateValidationOutcomeTest1() {
        EntityValidationOutcome entityValidationOutcome = generateEntityValidationOutcome(2, newDocUUID);

        Assert.assertTrue(service.updateValidationOutcome(entityValidationOutcome));
    }

    /**
     * Ignore entity validation result that refers to an older version and skip the validation outcome document update.
     */
    @Test
    public void updateValidationOutcomeTest2() {
        EntityValidationOutcome entityValidationOutcome = generateEntityValidationOutcome(1, oldDocUUID);

        Assert.assertTrue(!service.updateValidationOutcome(entityValidationOutcome));
    }

    private List<ValidationOutcome> generateValidationOutcomes() {
        List<ValidationOutcome> validationOutcomes = new ArrayList<>();

        Map<Archive, Boolean> archiveBooleanMap = new HashMap<>();
        archiveBooleanMap.put(Archive.BioSamples, false);
        archiveBooleanMap.put(Archive.ArrayExpress, false);
        archiveBooleanMap.put(Archive.Ena, false);

        // First
        ValidationOutcome vo1 = new ValidationOutcome();
        vo1.setUuid(UUID.randomUUID().toString());
        oldDocUUID = vo1.getUuid();
        vo1.setExpectedOutcomes(archiveBooleanMap);
        vo1.setVersion(1);
        vo1.setSubmissionId("123");
        vo1.setEntityUuid("44566");

        validationOutcomes.add(vo1);

        // Second
        ValidationOutcome vo2 = new ValidationOutcome();
        vo2.setUuid(UUID.randomUUID().toString());
        newDocUUID = vo2.getUuid();
        vo2.setExpectedOutcomes(archiveBooleanMap);
        vo2.setVersion(2);
        vo2.setSubmissionId("123");
        vo2.setEntityUuid("44566");

        validationOutcomes.add(vo2);

        return validationOutcomes;
    }

    private EntityValidationOutcome generateEntityValidationOutcome(int version, String docUUID) {
        EntityValidationOutcome evo = new EntityValidationOutcome();
        evo.setOutcomeDocumentUUID(docUUID);
        evo.setEntityUuid("44566");
        evo.setUuid(UUID.randomUUID().toString());
        evo.setArchive(Archive.BioSamples);
        evo.setVersion(version);
        return evo;
    }

}
