package uk.ac.ebi.subs.validator.coordinator;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.repository.model.Assay;
import uk.ac.ebi.subs.repository.model.Submission;
import uk.ac.ebi.subs.repository.repos.SubmissionRepository;
import uk.ac.ebi.subs.repository.repos.status.SubmissionStatusRepository;
import uk.ac.ebi.subs.repository.repos.submittables.AssayRepository;
import uk.ac.ebi.subs.validator.config.MongoDBDependentTest;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackageClasses = {AssayRepository.class, SubmissionRepository.class, SubmissionStatusRepository.class})
@Category(MongoDBDependentTest.class)
@EnableAutoConfiguration
@SpringBootTest(classes = AssayDataValidationMessageEnvelopeExpander.class)
public class AssayDataValidationMessageEnvelopeExpanderTest {

    @Autowired
    AssayRepository assayRepository;

    @Autowired
    SubmissionStatusRepository submissionStatusRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    AssayDataValidationMessageEnvelopeExpander assayDataValidationMessageEnvelopeExpander;

    Team team;
    @Test
    public void testExpandEnvelopeSameSubmissionByAccession() throws Exception {
        Team team = MesssageEnvelopeTestHelper.createTeam();
        final Submission submission= MesssageEnvelopeTestHelper.saveNewSubmission(submissionStatusRepository,submissionRepository,team);
        final Assay savedAssay = createAndSaveAssay(submission,team);
        AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope = createAssayDataValidationMessageEnvelope();
        AssayRef assayRef = new AssayRef();
        assayRef.setAccession(savedAssay.getAccession());
        assayDataValidationMessageEnvelope.getEntityToValidate().setAssayRef(assayRef);
        assayDataValidationMessageEnvelopeExpander.expandEnvelope(assayDataValidationMessageEnvelope,submission.getId());
        assertThat(savedAssay,is(assayDataValidationMessageEnvelope.getAssay()));
    }

    @Test
    public void testExpandEnvelopeSameSubmissionByAlias() throws Exception {
        Team team = MesssageEnvelopeTestHelper.createTeam();
        final Submission submission= MesssageEnvelopeTestHelper.saveNewSubmission(submissionStatusRepository,submissionRepository,team);
        final Assay savedAssay = createAndSaveAssay(submission,team);
        AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope = createAssayDataValidationMessageEnvelope();
        AssayRef assayRef = new AssayRef();
        assayRef.setAlias(savedAssay.getAlias());
        assayRef.setTeam(team.getName());
        assayDataValidationMessageEnvelope.getEntityToValidate().setAssayRef(assayRef);
        assayDataValidationMessageEnvelopeExpander.expandEnvelope(assayDataValidationMessageEnvelope,submission.getId());
        assertThat(savedAssay,is(assayDataValidationMessageEnvelope.getAssay()));

    }

    @Test
    public void testExpandEnvelopeSameSubmissionByAccessionDifferentSubmission() throws Exception {
        Team team = MesssageEnvelopeTestHelper.createTeam();
        final Submission submission= MesssageEnvelopeTestHelper.saveNewSubmission(submissionStatusRepository,submissionRepository,team);
        final Assay savedAssay = createAndSaveAssay(submission,team);
        AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope = createAssayDataValidationMessageEnvelope();
        AssayRef assayRef = new AssayRef();
        assayRef.setAccession(savedAssay.getAccession());
        assayDataValidationMessageEnvelope.getEntityToValidate().setAssayRef(assayRef);
        assayDataValidationMessageEnvelopeExpander.expandEnvelope(assayDataValidationMessageEnvelope,"SUB001");
        assertThat(assayDataValidationMessageEnvelope.getAssay(),is(nullValue()));

    }

    private AssayDataValidationMessageEnvelope createAssayDataValidationMessageEnvelope() {
        AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope = new AssayDataValidationMessageEnvelope();
        uk.ac.ebi.subs.data.submittable.AssayData assayData = new uk.ac.ebi.subs.data.submittable.AssayData();
        assayData.setTeam(team);
        assayData.setAccession(UUID.randomUUID().toString());
        assayData.setAlias(UUID.randomUUID().toString());
        assayDataValidationMessageEnvelope.setEntityToValidate(assayData);
        return assayDataValidationMessageEnvelope;
    }

    private Assay createAndSaveAssay (Submission submission, Team team) {
        Assay assay = new Assay();
        assay.setTeam(team);
        String accession = UUID.randomUUID().toString();
        String alias = UUID.randomUUID().toString();
        assay.setAlias(alias);
        assay.setAccession(accession);
        assay.setSubmission(submission);
        return assayRepository.save(assay);
    }

}