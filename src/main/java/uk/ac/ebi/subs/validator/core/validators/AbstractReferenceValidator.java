package uk.ac.ebi.subs.validator.core.validators;

import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

public abstract class AbstractReferenceValidator {

    String FAIL_MESSAGE = "Could not find reference target: %s .";
    String FAIL_TEAM_AND_ALIAS_MESSAGE = "Could not find reference for ALIAS: %s in TEAM: %s .";

    abstract void validate(AbstractSubsRef subsRef, SingleValidationResult singleValidationResult);

    void initializeSingleValidationResult(Submittable submittable, AbstractSubsRef abstractSubsRef, SingleValidationResult singleValidationResult) {
        if(submittable == null) {
            if(abstractSubsRef.getAccession() == null || abstractSubsRef.getAccession().isEmpty()) {
                singleValidationResult.setMessage(String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, abstractSubsRef.getAlias(), abstractSubsRef.getTeam()));
            } else {
                singleValidationResult.setMessage(String.format(FAIL_MESSAGE, abstractSubsRef.getAccession()));
            }
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);

        } else {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        }
    }

    void updateSingleValidationResult(Submittable submittable, AbstractSubsRef abstractSubsRef, SingleValidationResult singleValidationResult) {
        if(submittable == null) {
            StringBuilder message = new StringBuilder(singleValidationResult.getMessage());
            if(abstractSubsRef.getAccession() == null || abstractSubsRef.getAccession().isEmpty()) {
                message.append(" " + String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, abstractSubsRef.getAlias(), abstractSubsRef.getTeam()));
            } else {
                message.append(" " + String.format(FAIL_MESSAGE, abstractSubsRef.getAccession()));
            }

            singleValidationResult.setMessage(message.toString());
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
        }
    }

    void initializeSingleValidationResult(StringBuilder referencesTargets, SingleValidationResult singleValidationResult) {
        if (referencesTargets.toString().isEmpty()) {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        } else {
            singleValidationResult.setMessage(referencesTargets.toString());
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
        }
    }

    void updateSingleValidationResult(StringBuilder referencesTargets, SingleValidationResult singleValidationResult) {
        if (!referencesTargets.toString().isEmpty()) {
            StringBuilder message = new StringBuilder(singleValidationResult.getMessage());
            message.append(" " + referencesTargets);

            singleValidationResult.setMessage(message.toString());
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
        }
    }
}
