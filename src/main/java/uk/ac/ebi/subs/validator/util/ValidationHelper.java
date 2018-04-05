package uk.ac.ebi.subs.validator.util;

import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.List;

public final class ValidationHelper {

    public static boolean statusIsNotPassOrPending(SingleValidationResult r) {

        return !(r.getValidationStatus().equals(SingleValidationResultStatus.Pass)
                || r.getValidationStatus().equals(SingleValidationResultStatus.Pending));
    }

    public static SingleValidationResultsEnvelope generateSingleValidationResultsEnvelope(
            ValidationMessageEnvelope envelope, List<SingleValidationResult> singleValidationResults) {

        return new SingleValidationResultsEnvelope(
                singleValidationResults,
                envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(),
                ValidationAuthor.Core
        );
    }
}
