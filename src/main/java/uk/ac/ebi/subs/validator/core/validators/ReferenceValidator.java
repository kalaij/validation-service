package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReferenceValidator {
    String FAIL_MESSAGE = "Could not find reference target: %s ";
    String FAIL_TEAM_AND_ALIAS_MESSAGE = "Could not find reference for ALIAS: %s in TEAM: %s ";

    public void validate(Submittable submittable, AbstractSubsRef abstractSubsRef, SingleValidationResult singleValidationResult) {

        if (singleValidationResult.getValidationStatus().equals(SingleValidationResultStatus.Pending)) {
            initializeSingleValidationResult(submittable, abstractSubsRef, singleValidationResult);
        } else {
            updateSingleValidationResult(submittable, abstractSubsRef, singleValidationResult);
        }
    }

    public void validate(Submittable [] submittables, AbstractSubsRef [] abstractSubsRefs, SingleValidationResult singleValidationResult) {
        StringBuilder referencesTargets = new StringBuilder();
        final Map<String, Submittable> sampleAccessionMap = Arrays.stream(submittables).collect(
                Collectors.toMap(sample -> sample.getAccession(), sample -> sample));
        final Map<String, Submittable> sampleAliasMap = Arrays.stream(submittables).collect(
                Collectors.toMap(sample -> sample.getAlias() + sample.getTeam().getName(), sample -> sample));


        for (AbstractSubsRef abstractSubsRef : abstractSubsRefs) {

            Submittable submittable;

            if (abstractSubsRef.getAccession() != null && !abstractSubsRef.getAccession().isEmpty()) {
                submittable = sampleAccessionMap.get(abstractSubsRef.getAccession());
            } else {
                submittable = sampleAliasMap.get(abstractSubsRef.getAlias() + abstractSubsRef.getTeam());
            }

            if (submittable == null) {
                if (abstractSubsRef.getAccession() != null && !abstractSubsRef.getAccession().isEmpty()) {

                    if(referencesTargets.toString().isEmpty()) {
                        referencesTargets.append(String.format(FAIL_MESSAGE, abstractSubsRef.getAccession()));
                    } else {
                        referencesTargets.append(", " + String.format(FAIL_MESSAGE, abstractSubsRef.getAccession()));
                    }

                } else {

                    if(referencesTargets.toString().isEmpty()) {
                        referencesTargets.append(String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, abstractSubsRef.getAlias(), abstractSubsRef.getTeam()));
                    } else {
                        referencesTargets.append(", " + String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, abstractSubsRef.getAlias(), abstractSubsRef.getTeam()));
                    }
                }
            }

            if (singleValidationResult.getValidationStatus().equals(SingleValidationResultStatus.Pending)) {
                initializeSingleValidationResult(referencesTargets, singleValidationResult);
            } else {
                updateSingleValidationResult(referencesTargets, singleValidationResult);
            }
        }
    }

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
            if (singleValidationResult.getMessage() == null) {
                singleValidationResult.setMessage(referencesTargets.toString());
            } else {
                StringBuilder message = new StringBuilder(singleValidationResult.getMessage());

                message.append(" " + referencesTargets);

                singleValidationResult.setMessage(message.toString());
            }
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
        }

    }
}
