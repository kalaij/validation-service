package uk.ac.ebi.subs.validator.util;

import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.List;

public final class ValidationHelper {

    public static boolean statusIsNotPassOrPending(SingleValidationResult r) {

        return !(r.getValidationStatus().equals(SingleValidationResultStatus.Pass)
                || r.getValidationStatus().equals(SingleValidationResultStatus.Pending));
    }

    public static SingleValidationResultsEnvelope generateSingleValidationResultsEnvelope(
            int validationResultVersion, String validationResultUUID, List<SingleValidationResult> singleValidationResults,
            ValidationAuthor validationAuthor) {

        return new SingleValidationResultsEnvelope(
                singleValidationResults,
                validationResultVersion,
                validationResultUUID,
                validationAuthor
        );
    }

    public static SingleValidationResult generatePassingSingleValidationResult(String entityUuid, ValidationAuthor author) {
        SingleValidationResult validationResult = new SingleValidationResult();
        validationResult.setValidationAuthor(author);
        validationResult.setEntityUuid(entityUuid);

        validationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        return validationResult;
    }
}
