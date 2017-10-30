package uk.ac.ebi.subs.validator;

import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.data.ValidationResult;

import java.util.UUID;

/**
 * Created by karoly on 18/07/2017.
 */
public final class TestUtils {

    private static Team testTeam = Team.build("testTeam");

    public static Sample createSample() {
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

    public static ValidationResult createValidationResult(String submittableUuid) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setUuid(UUID.randomUUID().toString());
        validationResult.setSubmissionId("Test_submissionId");
        validationResult.setEntityUuid(submittableUuid);

        return validationResult;
    }
}
