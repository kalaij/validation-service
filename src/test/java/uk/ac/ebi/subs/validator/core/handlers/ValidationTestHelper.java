package uk.ac.ebi.subs.validator.core.handlers;

import org.junit.Assert;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.List;

final class ValidationTestHelper {

    static SingleValidationResult pass(String entityUuid) {
        return createResult(SingleValidationResultStatus.Pass, entityUuid);
    }

    static SingleValidationResult fail(String entityUuid) {
        return createResult(SingleValidationResultStatus.Error, entityUuid);
    }

    static List<SingleValidationResult> commonTestMethod(AbstractHandler handler, ValidationMessageEnvelope envelope,
                                        String validationResultId, int validationVersion, String entityUuid) {
        SingleValidationResultsEnvelope resultsEnvelope = handler.handleValidationRequest(envelope);

        commonEnvelopeAsserts(resultsEnvelope, validationResultId, envelope, validationVersion, entityUuid);

        return resultsEnvelope.getSingleValidationResults();
    }

    private static SingleValidationResult createResult(SingleValidationResultStatus status, String entityUuid) {
        SingleValidationResult result = new SingleValidationResult();
        result.setEntityUuid(entityUuid);
        result.setValidationStatus(status);
        result.setValidationAuthor(ValidationAuthor.Core);
        return result;
    }

    private static void commonEnvelopeAsserts(SingleValidationResultsEnvelope resultsEnvelope, String validationResultId,
                                      ValidationMessageEnvelope envelope, int validationVersion,
                                      String entityUuid) {
        Assert.assertNotNull(resultsEnvelope);
        Assert.assertNotNull(resultsEnvelope.getSingleValidationResults());
        Assert.assertEquals(ValidationAuthor.Core, resultsEnvelope.getValidationAuthor());
        Assert.assertEquals(validationResultId, envelope.getValidationResultUUID());
        Assert.assertEquals(validationVersion, envelope.getValidationResultVersion());

        for (SingleValidationResult result : resultsEnvelope.getSingleValidationResults()) {
            Assert.assertEquals(entityUuid, result.getEntityUuid());
        }
    }
}
