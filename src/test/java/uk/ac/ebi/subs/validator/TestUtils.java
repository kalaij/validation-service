package uk.ac.ebi.subs.validator;

import uk.ac.ebi.subs.data.component.File;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.repository.model.AssayData;
import uk.ac.ebi.subs.repository.model.Submission;
import uk.ac.ebi.subs.validator.data.ValidationResult;

import java.time.LocalDate;
import java.util.List;
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

    public static Sample createStaticSample() {
        return createStaticSampleWithReleaseDate(LocalDate.of(2018,1,1));
    }

    public static Sample createStaticSampleWithReleaseDate(LocalDate date) {
        Sample sample = new Sample();
        sample.setId("test-sample-id");
        sample.setTaxon("test-taxonomy");
        sample.setTaxonId(9606L);
        sample.setAccession("ABC12345");
        sample.setAlias("test-alias");
        sample.setDescription("Description for a test sample.");
        sample.setTeam(testTeam);
        sample.setReleaseDate(date);
        return sample;
    }

    public static ValidationResult createValidationResult(String submittableUuid) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setUuid(UUID.randomUUID().toString());
        validationResult.setSubmissionId("Test_submissionId");
        validationResult.setEntityUuid(submittableUuid);

        return validationResult;
    }

    public static Project createProject() {
        Project project = new Project();
        String id = UUID.randomUUID().toString();
        project.setId("TEST_PROJECT_" + id);
        project.setReleaseDate(LocalDate.now());
        return project;
    }

    public static AssayData createAssayData(String assayDataId, Submission submission, List<File> files) {
        AssayData assayData = new AssayData();
        assayData.setId(assayDataId);
        assayData.setSubmission(submission);
        assayData.setFiles(files);

        return assayData;
    }
}
