package uk.ac.ebi.subs.validator.filereference;

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
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.commonTestMethod;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.fail;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.getValidationResultFromFileReference;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.pass;

@RunWith(SpringRunner.class)
public class FileReferenceHandlerTest {

    private FileReferenceHandler fileReferenceHandler;

    @MockBean
    private FileReferenceValidator fileReferenceValidator;

    private final String submissionId = "subID";
    private final String assayDataId = "assayDataID";
    private final String validationResultId = "vrID";
    private final int validationVersion = 42;

    private AssayDataValidationMessageEnvelope envelope;

    private AssayRef assayRef;
    private SampleRef sampleRef;

    private Submittable<Assay> wrappedAssay;
    private Submittable<Sample> wrappedSample;


    @Before
    public void buildUp() {
        //setup the handler
        fileReferenceHandler = new FileReferenceHandler(fileReferenceValidator);

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
        envelope.setSubmissionId(submissionId);
    }

    @Test
    public void whenValidatorFails_ThenValidationResultStatusShouldBeError() {
        mockRefValidatorCalls(fail(assayDataId, ValidationAuthor.FileReference));

        SingleValidationResultsEnvelope resultsEnvelope = getValidationResultFromFileReference(fileReferenceHandler, envelope);

        List<SingleValidationResult> actualResults =
                commonTestMethod(resultsEnvelope, envelope, validationResultId, validationVersion, assayDataId,
                        ValidationAuthor.FileReference);

        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());

    }

    @Test
    public void whenValidatorPasses_ThenValidationResultStatusShouldBePass() {
        mockRefValidatorCalls(pass(assayDataId, ValidationAuthor.FileReference));

        SingleValidationResultsEnvelope resultsEnvelope = getValidationResultFromFileReference(fileReferenceHandler, envelope);

        List<SingleValidationResult> actualResults =
                commonTestMethod(resultsEnvelope, envelope, validationResultId, validationVersion, assayDataId,
                        ValidationAuthor.FileReference);

        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Pass, actualResults.get(0).getValidationStatus());
    }

    private void mockRefValidatorCalls(SingleValidationResult assayDataResult) {
        when(
                fileReferenceValidator.validate(submissionId)
        ).thenReturn(
                Collections.singletonList(assayDataResult)
        );

    }
}