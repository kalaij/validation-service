package uk.ac.ebi.subs.validator.core.handlers;

import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;

import java.util.UUID;

public interface Handler {

    SingleValidationResult handleValidationRequest(ValidationMessageEnvelope envelope);

    default SingleValidationResult generateBlankSingleValidationResult(String entityId, String validationResultUuid) {
        SingleValidationResult result = new SingleValidationResult(ValidationAuthor.Core, entityId);
        result.setUuid(UUID.randomUUID().toString());
        result.setValidationResultUUID(validationResultUuid);
        return result;
    }
}
