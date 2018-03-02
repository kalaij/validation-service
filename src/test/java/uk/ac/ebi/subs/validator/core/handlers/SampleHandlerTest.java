package uk.ac.ebi.subs.validator.core.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.ReferenceValidator;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
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
public class SampleHandlerTest {

    private SampleHandler sampleHandler;

    @MockBean
    private ReferenceValidator referenceValidator;

    @MockBean
    private AttributeValidator attributeValidator;

    private final String sampleId = "sampleId";
    private final String validationResultId = "vrID";
    private final int validationVersion = 42;

    private SampleValidationMessageEnvelope envelope;

    private SampleRelationship sampleRelationship;
    private Submittable<Sample> wrappedSample;


    @Before
    public void buildUp() {

        //setup the handler
        sampleHandler = new SampleHandler(referenceValidator, attributeValidator);

        //refs
        sampleRelationship = new SampleRelationship();

        //entity to be validated
        Sample sample = new Sample();
        sample.setId(sampleId);
        sample.setSampleRelationships(Arrays.asList(sampleRelationship));

        //reference data for the envelope
        Sample referencedSample = new Sample();
        String submissionId = "subID";
        wrappedSample = new Submittable<>(referencedSample, submissionId);

        //envelope
        envelope = new SampleValidationMessageEnvelope();
        envelope.setValidationResultUUID(validationResultId);
        envelope.setValidationResultVersion(validationVersion);
        envelope.setEntityToValidate(sample);
        envelope.setSampleList(Arrays.asList(wrappedSample));
    }

    @Test
    public void testHandler_pass() {
        mockValidatorCalls(pass());

        SingleValidationResultsEnvelope resultsEnvelope = sampleHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Pass, actualResults.get(0).getValidationStatus());
    }



    @Test
    public void testHandler_partialFailure() {
        mockValidatorCalls(fail(), pass());

        SingleValidationResultsEnvelope resultsEnvelope = sampleHandler.handleValidationRequest(envelope);


        commonEnvelopeAsserts(resultsEnvelope);

        List<SingleValidationResult> actualResults = resultsEnvelope.getSingleValidationResults();

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }


    @Test
    public void testHandler_bothFail() {
        mockValidatorCalls(fail(),fail());

        SingleValidationResultsEnvelope resultsEnvelope = sampleHandler.handleValidationRequest(envelope);


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
            Assert.assertEquals(sampleId, result.getEntityUuid());
        }

    }

    private void mockValidatorCalls(SingleValidationResult... sampleResults) {
        when(
                referenceValidator.validate(sampleId, Arrays.asList(sampleRelationship), Arrays.asList(wrappedSample))
        ).thenReturn(
                Arrays.asList(sampleResults)
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
        result.setEntityUuid(sampleId);
        result.setValidationStatus(status);
        result.setValidationAuthor(ValidationAuthor.Core);
        return result;
    }
}
