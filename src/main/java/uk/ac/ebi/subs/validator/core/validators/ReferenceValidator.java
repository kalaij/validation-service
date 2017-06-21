package uk.ac.ebi.subs.validator.core.validators;

import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

public interface ReferenceValidator {

    String FAIL_MESSAGE = "Could not find reference target: %s";

    void validate(AbstractSubsRef subsRef, SingleValidationResult singleValidationResult);

    default void initializeSingleValidationResult(Submittable submittable, AbstractSubsRef abstractSubsRef, SingleValidationResult singleValidationResult) {
        if(submittable == null) {
            singleValidationResult.setMessage(String.format(FAIL_MESSAGE, abstractSubsRef.getAccession()));
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
        } else {
            singleValidationResult.setValidationStatus(ValidationStatus.Pass);
        }
    }

    default void updateSingleValidationResult(Submittable submittable, AbstractSubsRef abstractSubsRef, SingleValidationResult singleValidationResult) {
        if(submittable == null) {
            StringBuilder message = new StringBuilder(singleValidationResult.getMessage());
            message.append(" " + String.format(FAIL_MESSAGE, abstractSubsRef.getAccession()));

            singleValidationResult.setMessage(message.toString());
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
        }
    }

    default void initializeSingleValidationResult(StringBuilder accessions, SingleValidationResult singleValidationResult) {
        if (accessions.toString().isEmpty()) {
            singleValidationResult.setValidationStatus(ValidationStatus.Pass);
        } else {
            singleValidationResult.setMessage(String.format(FAIL_MESSAGE, accessions));
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
        }
    }

    default void updateSingleValidationResult(StringBuilder accessions, SingleValidationResult singleValidationResult) {
        if (!accessions.toString().isEmpty()) {
            StringBuilder message = new StringBuilder(singleValidationResult.getMessage());
            message.append(" " + String.format(FAIL_MESSAGE, accessions));

            singleValidationResult.setMessage(message.toString());
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
        }
    }
}
