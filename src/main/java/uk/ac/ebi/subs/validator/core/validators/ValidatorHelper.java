package uk.ac.ebi.subs.validator.core.validators;

import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

public class ValidatorHelper {

    public static SingleValidationResult getDefaultSingleValidationResult(String id) {
        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Core, id);
        singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        return singleValidationResult;
    }
}
