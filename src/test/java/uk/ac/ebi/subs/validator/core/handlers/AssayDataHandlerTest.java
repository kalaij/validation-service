package uk.ac.ebi.subs.validator.core.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.ReferenceValidator;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.List;

import static org.mockito.Mockito.when;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.commonTestMethod;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.fail;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.getValidationResultFromSubmittables;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.pass;

@RunWith(SpringRunner.class)
public class AssayDataHandlerTest {

    private AssayDataHandler assayDataHandler;

    @MockBean
    private ReferenceValidator referenceValidator;

    @MockBean
    private AttributeValidator attributeValidator;

    private final String assayDataId = "assayDataID";
    private final String validationResultId = "vrID";
    private final int validationVersion = 42;
    private static final ValidationAuthor VALIDATION_AUTHOR_CORE = ValidationAuthor.Core;

    private AssayDataValidationMessageEnvelope envelope;

    private AssayRef assayRef;
    private SampleRef sampleRef;

    private Submittable<Assay> wrappedAssay;
    private Submittable<Sample> wrappedSample;

    @Before
    public void buildUp() {

        //setup the handler
        assayDataHandler = new AssayDataHandler(referenceValidator, attributeValidator);

        //refs
        assayRef = new AssayRef();
        sampleRef = new SampleRef();

        //entity to be validated
        AssayData assayData = new AssayData();
        assayData.setId(assayDataId);
        assayData.setAssayRef(assayRef);
        assayData.setSampleRef(sampleRef);

        //reference data for the envelope
        Assay assay = new Assay();
        String submissionId = "subID";
        wrappedAssay = new Submittable<>(assay, submissionId);
        Sample sample = new Sample();
        wrappedSample = new Submittable<>(sample, submissionId);

        //envelope
        envelope = new AssayDataValidationMessageEnvelope();
        envelope.setValidationResultUUID(validationResultId);
        envelope.setValidationResultVersion(validationVersion);
        envelope.setEntityToValidate(assayData);
        envelope.setAssay(wrappedAssay);
        envelope.setSample(wrappedSample);
    }

    @Test
    public void testHandler_bothRefCallsPass() {
        mockRefValidatorCalls(pass(assayDataId, VALIDATION_AUTHOR_CORE), pass(assayDataId, VALIDATION_AUTHOR_CORE));

        List<SingleValidationResult> actualResults =
                commonTestMethod(getValidationResultFromSubmittables(assayDataHandler, envelope),
                        envelope, validationResultId, validationVersion, assayDataId, VALIDATION_AUTHOR_CORE);

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Pass, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_sampleFails() {
        mockRefValidatorCalls(fail(assayDataId, VALIDATION_AUTHOR_CORE), pass(assayDataId, VALIDATION_AUTHOR_CORE));

        List<SingleValidationResult> actualResults =
                commonTestMethod(getValidationResultFromSubmittables(assayDataHandler, envelope),
                        envelope, validationResultId, validationVersion, assayDataId, VALIDATION_AUTHOR_CORE);

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_assayFails() {
        mockRefValidatorCalls(pass(assayDataId, VALIDATION_AUTHOR_CORE), fail(assayDataId, VALIDATION_AUTHOR_CORE));

        List<SingleValidationResult> actualResults =
                commonTestMethod(getValidationResultFromSubmittables(assayDataHandler, envelope),
                        envelope, validationResultId, validationVersion, assayDataId, VALIDATION_AUTHOR_CORE);

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_bothFail() {
        mockRefValidatorCalls(fail(assayDataId, VALIDATION_AUTHOR_CORE), fail(assayDataId, VALIDATION_AUTHOR_CORE));

        List<SingleValidationResult> actualResults =
                commonTestMethod(getValidationResultFromSubmittables(assayDataHandler, envelope),
                        envelope, validationResultId, validationVersion, assayDataId, VALIDATION_AUTHOR_CORE);

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(2, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(1).getValidationStatus());
    }

    private void mockRefValidatorCalls(SingleValidationResult assayResult, SingleValidationResult sampleResult) {
        when(
                referenceValidator.validate(assayDataId, sampleRef, wrappedSample)
        ).thenReturn(
                assayResult
        );

        when(
                referenceValidator.validate(assayDataId, assayRef, wrappedAssay)
        ).thenReturn(
                sampleResult
        );
    }
}
