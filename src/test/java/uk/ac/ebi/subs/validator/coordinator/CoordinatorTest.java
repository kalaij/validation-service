package uk.ac.ebi.subs.validator.coordinator;

import org.junit.Assert;
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
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.TestUtils;
import uk.ac.ebi.subs.validator.config.RabbitMQDependentTest;
import uk.ac.ebi.subs.validator.data.SubmittedProjectValidationEnvelope;
import uk.ac.ebi.subs.validator.data.SubmittedSampleValidationEnvelope;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.subs.validator.TestUtils.createProject;
import static uk.ac.ebi.subs.validator.TestUtils.createSample;
import static uk.ac.ebi.subs.validator.TestUtils.createValidationResult;

/**
 * Created by karoly on 30/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationResultRepository.class)
@EnableAutoConfiguration
@Category(RabbitMQDependentTest.class)
@SpringBootTest(classes =  {CoordinatorListener.class,CoordinatorValidationResultService.class})
public class CoordinatorTest {

    @Autowired
    private CoordinatorListener coordinator;

    @Autowired
    private ValidationResultRepository validationResultRepository;

    private Sample sample;
    private Project project;

    @Before
    public void setUp() {
        validationResultRepository.deleteAll();

        sample = TestUtils.createSample();
        validationResultRepository.save(createValidationResult(sample.getId()));

        project = createProject();
        validationResultRepository.save(createValidationResult(project.getId()));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSubmissionWithoutContent() {
        String expectedErrorMessage = "The envelop should contain a sample.";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        SubmittedSampleValidationEnvelope submittableEnvelopWithoutSample = createSubmittableEnvelopeWithoutSample();
        coordinator.processSampleSubmission(submittableEnvelopWithoutSample);
    }

    @Test
    public void testSubmissionWithSample() {
        SubmittedSampleValidationEnvelope submittableValidationEnvelope = createSubmittableEnvelopeWithSample(sample);

        try {
            coordinator.processSampleSubmission(submittableValidationEnvelope);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void handleDeletedSubmittable() {
        Sample sample = createSample();
        SubmittedSampleValidationEnvelope envelope = new SubmittedSampleValidationEnvelope();
        envelope.setEntityToValidate(sample);

        try {
            coordinator.processSampleSubmission(envelope);
        } catch (IllegalStateException exception) {
            assertTrue(exception.getMessage().startsWith("Could not find ValidationResult for submittable with ID"));
        }
    }

    @Test
    public void testSubmissionWithProject() {
        SubmittedProjectValidationEnvelope envelope = new SubmittedProjectValidationEnvelope();
        envelope.setEntityToValidate(project);

        try {
            coordinator.processProjectSubmission(envelope);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private SubmittedSampleValidationEnvelope createSubmittableEnvelopeWithoutSample() {
        String submissionId = "testSubmissionId1";

        SubmittedSampleValidationEnvelope envelope = new SubmittedSampleValidationEnvelope();
        envelope.setSubmissionId(submissionId);

        return envelope;
    }

    private SubmittedSampleValidationEnvelope createSubmittableEnvelopeWithSample(Sample sample) {
        SubmittedSampleValidationEnvelope envelope = createSubmittableEnvelopeWithoutSample();
        envelope.setEntityToValidate(sample);

        return envelope;
    }

}
