package uk.ac.ebi.subs.validator.core.handlers;

import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.List;
import java.util.UUID;

public abstract class AbstractHandler {

    abstract SingleValidationResultsEnvelope handleValidationRequest(ValidationMessageEnvelope envelope);

    SingleValidationResult generateBlankSingleValidationResult(String entityId, String validationResultUuid) {
        SingleValidationResult result = new SingleValidationResult(ValidationAuthor.Core, entityId);
        result.setUuid(UUID.randomUUID().toString());
        result.setValidationResultUUID(validationResultUuid);
        return result;
    }

    SingleValidationResultsEnvelope generateSingleValidationResultsEnvelope(ValidationMessageEnvelope envelope, List<SingleValidationResult> singleValidationResults) {
        return new SingleValidationResultsEnvelope(
                singleValidationResults,
                envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(),
                ValidationAuthor.Core
        );
    }

    void checkValidationStatus(SingleValidationResult singleValidationResult) {
        if (singleValidationResult.getValidationStatus().equals(ValidationStatus.Pending)) {
            singleValidationResult.setValidationStatus(ValidationStatus.Pass);
        }
    }
}
