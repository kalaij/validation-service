package uk.ac.ebi.subs.validator.coordinator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.coordinator.config.RabbitMQDependentTest;
import uk.ac.ebi.subs.validator.data.SubmittableValidationEnvelope;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

/**
 * Created by karoly on 30/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationResultRepository.class)
@EnableAutoConfiguration
@Category(RabbitMQDependentTest.class)
@SpringBootTest("uk.ac.ebi.subs.validator")
public class CoordinatorTest {

    @Autowired
    private Coordinator coordinator;

    @Autowired
    private ValidationResultRepository validationResultRepository;

    private Sample sample;

    @Before
    public void setUp() {
        validationResultRepository.deleteAll();

        sample = TestUtils.createSample();

        validationResultRepository.save(TestUtils.createValidationResult(sample.getId()));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSubmissionWithoutContent() {
        String expectedErrorMessage = "The envelop should contain a sample.";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        SubmittableValidationEnvelope submittableEnvelopWithoutSample = createSubmittableEnvelopeWithoutSample();
        coordinator.processSampleSubmission(submittableEnvelopWithoutSample);
    }

    @Test
    public void testSubmissionWithSample() {
        SubmittableValidationEnvelope<Sample> submittableValidationEnvelope = createSubmittableEnvelopeWithSample(sample);

        coordinator.processSampleSubmission(submittableValidationEnvelope);
    }

    private SubmittableValidationEnvelope createSubmittableEnvelopeWithoutSample() {
        String submissionId = "testSubmissionId1";

        return new SubmittableValidationEnvelope(submissionId, null);
    }

    private SubmittableValidationEnvelope createSubmittableEnvelopeWithSample(Sample sample) {
        String submissionId = "testSubmissionId1";

       return new SubmittableValidationEnvelope(submissionId, sample);
    }
}
