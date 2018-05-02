package uk.ac.ebi.subs.validator.filereference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.fileupload.File;
import uk.ac.ebi.subs.data.fileupload.FileStatus;
import uk.ac.ebi.subs.validator.data.FileUploadValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.commonTestMethodForFiles;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.fail;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.getValidationResultForUploadedFileFileReference;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.pass;

@RunWith(SpringRunner.class)
public class UploadedFileFileReferenceHandlerTest {

    private FileReferenceHandler fileReferenceHandler;

    @MockBean
    private FileReferenceValidator fileReferenceValidator;

    private final String submissionId = "subID";
    private final String validationResultId = "vrID";
    private final int validationVersion = 42;

    private final String FILE_ID = "1234567890";
    private final String FILE_NAME = "test.cram";

    private FileUploadValidationMessageEnvelope envelope;
    private File file;

    @Before
    public void buildUp() {
        //setup the handler
        fileReferenceHandler = new FileReferenceHandler(fileReferenceValidator);

        // uploaded file
        file = new File();
        file.setId(FILE_ID);
        file.setFilename(FILE_NAME);
        file.setStatus(FileStatus.READY_FOR_ARCHIVE);
        file.setSubmissionId(submissionId);

        //envelope
        envelope = new FileUploadValidationMessageEnvelope();
        envelope.setValidationResultUUID(validationResultId);
        envelope.setValidationResultVersion(validationVersion);
        envelope.setfileToValidate(file);
        envelope.setSubmissionId(submissionId);
    }

    @Test
    public void whenValidatorFails_ThenValidationResultStatusShouldBeError() {
        mockRefValidatorCalls(fail(FILE_ID, ValidationAuthor.FileReference));

        SingleValidationResultsEnvelope resultsEnvelope =
                getValidationResultForUploadedFileFileReference(fileReferenceHandler, envelope);

        List<SingleValidationResult> actualResults =
                commonTestMethodForFiles(resultsEnvelope, envelope, validationResultId, validationVersion, FILE_ID,
                        ValidationAuthor.FileReference);

        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());

    }

    @Test
    public void whenValidatorPasses_ThenValidationResultStatusShouldBePass() {
        mockRefValidatorCalls(pass(FILE_ID, ValidationAuthor.FileReference));

        SingleValidationResultsEnvelope resultsEnvelope =
                getValidationResultForUploadedFileFileReference(fileReferenceHandler, envelope);

        List<SingleValidationResult> actualResults =
                commonTestMethodForFiles(resultsEnvelope, envelope, validationResultId, validationVersion, FILE_ID,
                        ValidationAuthor.FileReference);

        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Pass, actualResults.get(0).getValidationStatus());
    }

    private void mockRefValidatorCalls(SingleValidationResult fileValidationResult) {
        when(
                fileReferenceValidator.validate(file)
        ).thenReturn(
                Collections.singletonList(fileValidationResult)
        );

    }
}