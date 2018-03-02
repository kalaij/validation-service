package uk.ac.ebi.subs.validator.core.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.ReferenceValidator;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class AssayHandlerTest {

    private AssayHandler assayHandler;

    @MockBean
    private ReferenceValidator referenceValidator;

    @MockBean
    private AttributeValidator attributeValidator;

    private final String assayId = "assayId";
    private final String validationResultId = "vrID";
    private final int validationVersion = 42;

    private AssayValidationMessageEnvelope envelope;

    private StudyRef studyRef;
    private SampleRef sampleRef;


    private Submittable<Sample> wrappedSample;
    private Submittable<Study> wrappedStudy;


    @Before
    public void buildUp() {

        //setup the handler
        assayHandler = new AssayHandler(referenceValidator, attributeValidator);

        //refs
        studyRef = new StudyRef();
        sampleRef = new SampleRef();

        SampleUse sampleUse = new SampleUse();
        sampleUse.setSampleRef(sampleRef);

        //entity to be validated
        Assay assay = new Assay();
        assay.setId(assayId);
        assay.setStudyRef(studyRef);
        assay.setSampleUses(Arrays.asList(
                sampleUse
        ));

        //reference data for the envelope
        Study study = new Study();
        String submissionId = "subID";
        wrappedStudy = new Submittable<>(study, submissionId);
        Sample sample = new Sample();
        wrappedSample = new Submittable<>(sample, submissionId);

        //envelope
        envelope = new AssayValidationMessageEnvelope();
        envelope.setValidationResultUUID(validationResultId);
        envelope.setValidationResultVersion(validationVersion);
        envelope.setEntityToValidate(assay);
        envelope.setStudy(wrappedStudy);
        envelope.setSampleList(Arrays.asList(wrappedSample));
    }

    @Test
    public void testHandler_bothRefCallsPass() {
        mockRefValidatorCalls(pass(), pass());

        SingleValidationResultsEnvelope resultsEnvelope = assayHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Pass, actualResults.get(0).getValidationStatus());
    }



    @Test
    public void testHandler_sampleFails() {
        mockRefValidatorCalls(fail(), pass());

        SingleValidationResultsEnvelope resultsEnvelope = assayHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_assayFails() {
        mockRefValidatorCalls(pass(), fail());

        SingleValidationResultsEnvelope resultsEnvelope = assayHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_bothFail() {
        mockRefValidatorCalls(fail(),fail());

        SingleValidationResultsEnvelope resultsEnvelope = assayHandler.handleValidationRequest(envelope);


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
            Assert.assertEquals(assayId, result.getEntityUuid());
        }

    }

    private void mockRefValidatorCalls(SingleValidationResult studyResult, SingleValidationResult sampleresult) {
        when(
                referenceValidator.validate(assayId, studyRef, wrappedStudy)
        ).thenReturn(
                studyResult
        );

        when(
                referenceValidator.validate(assayId, Arrays.asList(sampleRef), Arrays.asList(wrappedSample))
        ).thenReturn(
                Arrays.asList(sampleresult)
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
        result.setEntityUuid(assayId);
        result.setValidationStatus(status);
        result.setValidationAuthor(ValidationAuthor.Core);
        return result;
    }
}