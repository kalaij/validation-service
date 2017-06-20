package uk.ac.ebi.subs.validator.core.validators;

import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

public interface ReferenceValidator {

    String FAIL_MESSAGE = "Could not find reference target: %s";
    String SUCCESS_MESSAGE = "Reference found.";

    void validate(AbstractSubsRef subsRef, SingleValidationResult singleValidationResult);

    default void updateSingleValidationResult(Submittable submittable, AbstractSubsRef abstractSubsRef, SingleValidationResult singleValidationResult) {
        if(submittable == null) {
            singleValidationResult.setMessage(String.format(FAIL_MESSAGE, abstractSubsRef.getAccession()));
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
        } else {
            singleValidationResult.setMessage(SUCCESS_MESSAGE);
            singleValidationResult.setValidationStatus(ValidationStatus.Pass);
        }
    }

    default void updateSingleValidationResult(StringBuilder accessions, SingleValidationResult singleValidationResult) {
        if (accessions.toString().isEmpty()) {
            singleValidationResult.setMessage(SUCCESS_MESSAGE);
            singleValidationResult.setValidationStatus(ValidationStatus.Pass);
        } else {
            singleValidationResult.setMessage(String.format(FAIL_MESSAGE, accessions));
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
        }
    }
}
