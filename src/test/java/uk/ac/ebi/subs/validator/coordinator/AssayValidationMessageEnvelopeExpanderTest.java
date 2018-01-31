package uk.ac.ebi.subs.validator.coordinator;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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



    @Test
    public void testExpandEnvelopeSameSubmissionByAccession() throws Exception {
        Team team = MesssageEnvelopeTestHelper.createTeam();
        final Submission submission= MesssageEnvelopeTestHelper.saveNewSubmission(submissionStatusRepository,submissionRepository,team);
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();
        final Study savedStudy = createAndSaveStudy(submission,team);

        final List<Sample> savedSampleList = createAndSaveSamples(submission, team, 1);

        for (Sample sample : savedSampleList) {
            SampleRef sampleRef = new SampleRef();
            sampleRef.setAccession(sample.getAccession());
            assayValidationMessageEnvelope.getEntityToValidate().getSampleUses().add(new SampleUse(sampleRef));
        }

        StudyRef studyRef = new StudyRef();
        studyRef.setAccession(savedStudy.getAccession());
        assayValidationMessageEnvelope.getEntityToValidate().setStudyRef(studyRef);
        assayValidatorMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope,submission.getId());
        assertThat(savedStudy,is(assayValidationMessageEnvelope.getStudy()));
        assertThat(savedSampleList, is(assayValidationMessageEnvelope.getSampleList()));
    }

    @Test
    public void testExpandEnvelopeSameSubmissionByAlias() throws Exception {
        Team team = MesssageEnvelopeTestHelper.createTeam();
        final Submission submission= MesssageEnvelopeTestHelper.saveNewSubmission(submissionStatusRepository,submissionRepository,team);
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();
        final Study savedStudy = createAndSaveStudy(submission,team);

        final List<Sample> savedSampleList = createAndSaveSamples(submission, team, 1);

        for (Sample sample : savedSampleList) {
            SampleRef sampleRef = new SampleRef();
            sampleRef.setAlias(sample.getAlias());
            sampleRef.setTeam(team.getName());
            assayValidationMessageEnvelope.getEntityToValidate().getSampleUses().add(new SampleUse(sampleRef));
        }

        StudyRef studyRef = new StudyRef();
        studyRef.setAccession(savedStudy.getAccession());
        assayValidationMessageEnvelope.getEntityToValidate().setStudyRef(studyRef);
        assayValidatorMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope,submission.getId());
        assertThat(savedStudy,is(assayValidationMessageEnvelope.getStudy()));
        assertThat(savedSampleList, is(assayValidationMessageEnvelope.getSampleList()));
    }

    @Test
    public void testExpandEnvelopeSameSubmissionByAccessionDifferentSubmission() throws Exception {
        Team team = MesssageEnvelopeTestHelper.createTeam();
        final Submission submission= MesssageEnvelopeTestHelper.saveNewSubmission(submissionStatusRepository,submissionRepository,team);
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();
        final Study savedStudy = createAndSaveStudy(submission,team);

        final List<Sample> savedSampleList = createAndSaveSamples(submission, team, 1);

        for (Sample sample : savedSampleList) {
            SampleRef sampleRef = new SampleRef();
            sampleRef.setAccession(sample.getAccession());
            assayValidationMessageEnvelope.getEntityToValidate().getSampleUses().add(new SampleUse(sampleRef));
        }

        StudyRef studyRef = new StudyRef();
        studyRef.setAccession(savedStudy.getAccession());
        assayValidationMessageEnvelope.getEntityToValidate().setStudyRef(studyRef);
        assayValidatorMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope,"SUB001");
        assertThat(assayValidationMessageEnvelope.getStudy(),is(nullValue()));
        assertThat(assayValidationMessageEnvelope.getSampleList().isEmpty(), is(true));
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

    private List<Sample> createAndSaveSamples (Submission submission, Team team, int sampleNumber) {
        List<Sample> sampleList = new ArrayList<>(sampleNumber);
        for (int i = 0; i < sampleNumber; i++ ) {
            Sample sample = new Sample();
            sample.setTeam(team);
            String alias = UUID.randomUUID().toString();
            String accession = UUID.randomUUID().toString();
            sample.setAlias(alias);
            sample.setAccession(accession);
            sample.setSubmission(submission);
            sampleList.add(sampleRepository.save(sample));
        }
        return sampleList;
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