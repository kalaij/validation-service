package uk.ac.ebi.subs.validator.coordinator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.TestUtils;
import uk.ac.ebi.subs.validator.config.MongoDBDependentTest;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationResultRepository.class)
@Category(MongoDBDependentTest.class)
@EnableAutoConfiguration
@SpringBootTest(classes = CoordinatorValidationResultService.class)
public class CoordinatorValidationResultServiceTest {

    @Autowired
    ValidationResultRepository repository;

    @Autowired
    CoordinatorValidationResultService service;

    private Sample sample;

    @Before
    public void setUp() {
        repository.deleteAll();

        sample = TestUtils.createSample();

        repository.save(TestUtils.createValidationResult(sample.getId()));
    }

    @Test
    public void getVersionTest() {
        ValidationResult validationResult = new ValidationResult();

        for (int i = 0; i < 5; i++) {
            validationResult = service.fetchValidationResultDocument(sample).get();
        }

        Assert.assertEquals(5, validationResult.getVersion());
    }
}
