package uk.ac.ebi.subs.validator.coordinator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.repository.model.*;
import uk.ac.ebi.subs.repository.repos.SubmissionRepository;
import uk.ac.ebi.subs.repository.repos.status.SubmissionStatusRepository;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;
import uk.ac.ebi.subs.repository.repos.submittables.StudyRepository;
import uk.ac.ebi.subs.validator.config.MongoDBDependentTest;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = {SampleRepository.class, StudyRepository.class, SubmissionRepository.class, SubmissionStatusRepository.class})
@Category(MongoDBDependentTest.class)
@EnableAutoConfiguration
@SpringBootTest(classes = AssayValidationMessageEnvelopeExpander.class)
public class AssayValidationMessageEnvelopeExpanderTest {

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    SampleRepository sampleRepository;

    @Autowired
    SubmissionStatusRepository submissionStatusRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    AssayValidationMessageEnvelopeExpander assayValidatorMessageEnvelopeExpander;

    Team team;
    Submission submission;
    List<Sample> savedSampleList;
    Study savedStudy;

    @Before
    public void setup() {
        team = MesssageEnvelopeTestHelper.createTeam();
        submission = MesssageEnvelopeTestHelper.saveNewSubmission(submissionStatusRepository, submissionRepository, team);
        savedStudy = createAndSaveStudy(submission,team);
        savedSampleList = MesssageEnvelopeTestHelper.createAndSaveSamples(sampleRepository, submission, team, 1);
    }

    @After
    public void finish() {
        studyRepository.delete(savedStudy);
        sampleRepository.delete(savedSampleList);
        submissionRepository.delete(submission);
        submissionStatusRepository.delete(submission.getSubmissionStatus());
    }


    @Test
    public void testExpandEnvelopeSameSubmissionByAccessionForStudy() throws Exception {
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();
        StudyRef studyRef = new StudyRef();
        studyRef.setAccession(savedStudy.getAccession());
        assayValidationMessageEnvelope.getEntityToValidate().setStudyRef(studyRef);
        assayValidatorMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope);
        assertThat(savedStudy,is(assayValidationMessageEnvelope.getStudy().getBaseSubmittable()));
    }

    @Test
    public void testExpandEnvelopeSameSubmissionByAccessionForSampleList() throws Exception {
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();

        for (Sample sample : savedSampleList) {
            SampleRef sampleRef = new SampleRef();
            sampleRef.setAccession(sample.getAccession());
            assayValidationMessageEnvelope.getEntityToValidate().getSampleUses().add(new SampleUse(sampleRef));
        }

        assayValidatorMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope);
        final List<uk.ac.ebi.subs.data.submittable.Sample> sampleList = assayValidationMessageEnvelope.getSampleList().stream().map(Submittable::getBaseSubmittable).collect(Collectors.toList());
        assertThat(savedSampleList, is(sampleList));
    }

    @Test
    public void testExpandEnvelopeSameSubmissionByAliasForStudy() throws Exception {
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();
        StudyRef studyRef = new StudyRef();
        studyRef.setAlias(savedStudy.getAlias());
        studyRef.setTeam(savedStudy.getTeam().getName());
        assayValidationMessageEnvelope.getEntityToValidate().setStudyRef(studyRef);
        assayValidatorMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope);
        assertThat(savedStudy,is(assayValidationMessageEnvelope.getStudy().getBaseSubmittable()));
    }

    @Test
    public void testExpandEnvelopeSameSubmissionByAliasForSampleList() throws Exception {
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();

        for (Sample sample : savedSampleList) {
            SampleRef sampleRef = new SampleRef();
            sampleRef.setAlias(sample.getAlias());
            sampleRef.setTeam(team.getName());
            assayValidationMessageEnvelope.getEntityToValidate().getSampleUses().add(new SampleUse(sampleRef));
        }

        assayValidatorMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope);
        final List<uk.ac.ebi.subs.data.submittable.Sample> sampleList = assayValidationMessageEnvelope.getSampleList().stream().map(Submittable::getBaseSubmittable).collect(Collectors.toList());
        assertThat(savedSampleList, is(sampleList));
    }

    private AssayValidationMessageEnvelope createAssayValidationMessageEnvelope() {
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = new AssayValidationMessageEnvelope();
        uk.ac.ebi.subs.data.submittable.Assay submittableAssay = new uk.ac.ebi.subs.data.submittable.Assay();
        submittableAssay.setTeam(team);
        submittableAssay.setAccession(UUID.randomUUID().toString());
        submittableAssay.setAlias(UUID.randomUUID().toString());
        assayValidationMessageEnvelope.setEntityToValidate(submittableAssay);
        return assayValidationMessageEnvelope;
    }

    private Study createAndSaveStudy (Submission submission, Team team) {
        Study study = new Study();
        study.setTeam(team);
        String projectAccession = UUID.randomUUID().toString();
        String projectAlias = UUID.randomUUID().toString();
        study.setAlias(projectAlias);
        study.setAccession(projectAccession);
        study.setSubmission(submission);
        return studyRepository.save(study);
    }

}