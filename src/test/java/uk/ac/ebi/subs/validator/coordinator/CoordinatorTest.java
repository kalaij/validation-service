package uk.ac.ebi.subs.validator.coordinator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.data.SubmittableValidationEnvelope;
import uk.ac.ebi.subs.validator.repository.ValidationOutcomeRepository;

import java.util.UUID;

/**
 * Created by karoly on 30/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = ValidationOutcomeRepository.class)
@EnableAutoConfiguration
@SpringBootTest(classes = { OutcomeDocumentService.class, Coordinator.class })
public class CoordinatorTest {

    @Autowired
    ValidationOutcomeRepository repository;

    @Autowired
    private Coordinator coordinator;

    private Team testTeam = Team.build("testTeam");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSubmissionWithoutContent() {
        String expectedErrorMessage = "The envelop should contain a sample.";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        SubmittableValidationEnvelope<Sample> submittableEnvelopWithoutSample = createSubmittableEnvelopeWithoutSample();
        coordinator.processSampleSubmission(submittableEnvelopWithoutSample);
    }

    @Test
    public void testSubmissionWithContent() {
        SubmittableValidationEnvelope<Sample> submittableEnvelopWithoutSample = createSubmittableEnvelopeWithSample();
        coordinator.processSampleSubmission(submittableEnvelopWithoutSample);
    }

    private SubmittableValidationEnvelope<Sample> createSubmittableEnvelopeWithoutSample() {
        String submissionId = "testSubmissionId1";

        return new SubmittableValidationEnvelope<>(submissionId, null);
    }

    private SubmittableValidationEnvelope<Sample> createSubmittableEnvelopeWithSample() {
        String submissionId = "testSubmissionId1";

        return new SubmittableValidationEnvelope<>(submissionId, createSample());
    }

    private Sample createSample() {
        Sample sample = new Sample();
        String id = UUID.randomUUID().toString();
        sample.setId("TEST_SAMPLE_" + id);
        sample.setTaxon("testTaxon_" + id);
        sample.setTaxonId(1234L);
        sample.setAccession("ABC_" + id);
        sample.setAlias("TestAlias_" + id);
        sample.setDescription("Description for sample with id: " + id);
        sample.setTeam(testTeam);

        return sample;
    }
}
