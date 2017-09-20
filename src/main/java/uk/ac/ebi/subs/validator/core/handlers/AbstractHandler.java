package uk.ac.ebi.subs.validator.core.handlers;

import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.List;

public abstract class AbstractHandler {

    abstract SingleValidationResultsEnvelope handleValidationRequest(ValidationMessageEnvelope envelope);

    SingleValidationResultsEnvelope generateSingleValidationResultsEnvelope(ValidationMessageEnvelope envelope, List<SingleValidationResult> singleValidationResults) {
        return new SingleValidationResultsEnvelope(
                singleValidationResults,
                envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(),
                ValidationAuthor.Core
        );
    }

    void checkValidationStatus(SingleValidationResult singleValidationResult) {
        if (singleValidationResult.getValidationStatus().equals(SingleValidationResultStatus.Pending)) {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        }
    }
}
