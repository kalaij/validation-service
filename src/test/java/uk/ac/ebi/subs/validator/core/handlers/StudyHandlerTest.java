package uk.ac.ebi.subs.validator.core.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.ProjectRef;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.ReferenceValidator;
import uk.ac.ebi.subs.validator.core.validators.StudyTypeValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class StudyHandlerTest {

    private StudyHandler studyHandler;

    @MockBean
    private ReferenceValidator referenceValidator;

    @MockBean
    private AttributeValidator attributeValidator;

    @MockBean
    private StudyTypeValidator studyTypeValidator;

    private final String studyId = "studyId";
    private final String validationResultId = "vrID";
    private final int validationVersion = 42;

    private StudyValidationMessageEnvelope envelope;

    private ProjectRef projectRef;
    private Submittable<Project> wrappedProject;

    private Study study;


    @Before
    public void buildUp() {

        //setup the handler
        studyHandler = new StudyHandler(studyTypeValidator, attributeValidator, referenceValidator);

        //refs
        projectRef = new ProjectRef();

        //entity to be validated
        study = new Study();
        study.setId(studyId);
        study.setProjectRef(projectRef);
        study.setStudyType(StudyDataType.Metabolomics);

        //reference data for the envelope
        Project project = new Project();
        String submissionId = "subID";
        wrappedProject = new Submittable<>(project, submissionId);

        //envelope
        envelope = new StudyValidationMessageEnvelope();
        envelope.setValidationResultUUID(validationResultId);
        envelope.setValidationResultVersion(validationVersion);
        envelope.setEntityToValidate(study);
        envelope.setProject(wrappedProject);
    }

    @Test
    public void testHandler_bothCallsPass() {
        mockValidatorCalls(pass(), pass());

        SingleValidationResultsEnvelope resultsEnvelope = studyHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Pass, actualResults.get(0).getValidationStatus());
    }



    @Test
    public void testHandler_projectFails() {
        mockValidatorCalls(fail(), pass());

        SingleValidationResultsEnvelope resultsEnvelope = studyHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_studyTypeFails() {
        mockValidatorCalls(pass(), fail());

        SingleValidationResultsEnvelope resultsEnvelope = studyHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_bothFail() {
        mockValidatorCalls(fail(),fail());

        SingleValidationResultsEnvelope resultsEnvelope = studyHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(2, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(1).getValidationStatus());
    }

    private void commonEnvelopeAsserts(SingleValidationResultsEnvelope resultsEnvelope) {
        Assert.assertNotNull(resultsEnvelope);
        Assert.assertNotNull(resultsEnvelope.getSingleValidationResults());
        Assert.assertEquals(ValidationAuthor.Core, resultsEnvelope.getValidationAuthor());
        Assert.assertEquals(validationResultId, envelope.getValidationResultUUID());
        Assert.assertEquals(validationVersion, envelope.getValidationResultVersion());

        for (SingleValidationResult result : resultsEnvelope.getSingleValidationResults()) {
            Assert.assertEquals(studyId, result.getEntityUuid());
        }

    }

    private void mockValidatorCalls(SingleValidationResult projectResult, SingleValidationResult studyTypeResult) {
        when(
                referenceValidator.validate(studyId, projectRef, wrappedProject)
        ).thenReturn(
                projectResult
        );

        when(
                studyTypeValidator.validate(study)
        ).thenReturn(
                studyTypeResult
        );
    }
    
    private SingleValidationResult pass() {
        return createResult(SingleValidationResultStatus.Pass);
    }

    private SingleValidationResult fail() {
        return createResult(SingleValidationResultStatus.Error);
    }

    private SingleValidationResult createResult(SingleValidationResultStatus status) {
        SingleValidationResult result = new SingleValidationResult();
        result.setEntityUuid(studyId);
        result.setValidationStatus(status);
        result.setValidationAuthor(ValidationAuthor.Core);
        return result;
    }
}
