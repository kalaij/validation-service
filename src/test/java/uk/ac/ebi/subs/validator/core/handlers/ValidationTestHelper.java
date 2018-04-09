package uk.ac.ebi.subs.validator.core.handlers;

import org.junit.Assert;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.filereference.FileReferenceHandler;

import java.util.List;

public final class ValidationTestHelper {

    public static SingleValidationResult pass(String entityUuid, ValidationAuthor validationAuthor) {
        return createResult(SingleValidationResultStatus.Pass, entityUuid, validationAuthor);
    }

    public static SingleValidationResult fail(String entityUuid, ValidationAuthor validationAuthor) {
        return createResult(SingleValidationResultStatus.Error, entityUuid, validationAuthor);
    }

    public static SingleValidationResultsEnvelope getValidationResultFromSubmittables(
            AbstractHandler handler, ValidationMessageEnvelope envelope) {

        return handler.handleValidationRequest(envelope);
    };

    public static SingleValidationResultsEnvelope getValidationResultFromFileReference(
            FileReferenceHandler handler, AssayDataValidationMessageEnvelope envelope) {

        return handler.handleValidationRequest(envelope);
    };

    public static List<SingleValidationResult> commonTestMethod(SingleValidationResultsEnvelope resultsEnvelope,
                                                                ValidationMessageEnvelope envelope,
                                                                String validationResultId, int validationVersion,
                                                                String entityUuid, ValidationAuthor validationAuthor) {

        commonEnvelopeAsserts(resultsEnvelope, validationResultId, envelope, validationVersion, entityUuid, validationAuthor);

        return resultsEnvelope.getSingleValidationResults();
    }

    private static SingleValidationResult createResult(SingleValidationResultStatus status, String entityUuid,
                                                       ValidationAuthor validationAuthor) {
        SingleValidationResult result = new SingleValidationResult();
        result.setEntityUuid(entityUuid);
        result.setValidationStatus(status);
        result.setValidationAuthor(validationAuthor);
        return result;
    }

    private static void commonEnvelopeAsserts(SingleValidationResultsEnvelope resultsEnvelope, String validationResultId,
                                      ValidationMessageEnvelope envelope, int validationVersion,
                                      String entityUuid, ValidationAuthor validationAuthor) {
        Assert.assertNotNull(resultsEnvelope);
        Assert.assertNotNull(resultsEnvelope.getSingleValidationResults());
        Assert.assertEquals(validationAuthor, resultsEnvelope.getValidationAuthor());
        Assert.assertEquals(validationResultId, envelope.getValidationResultUUID());
        Assert.assertEquals(validationVersion, envelope.getValidationResultVersion());

        for (SingleValidationResult result : resultsEnvelope.getSingleValidationResults()) {
            Assert.assertEquals(entityUuid, result.getEntityUuid());
        }
    }
}
