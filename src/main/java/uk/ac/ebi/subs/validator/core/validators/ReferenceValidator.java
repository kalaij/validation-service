package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.validator.core.validators.ValidatorHelper.getDefaultSingleValidationResult;

@Service
public class ReferenceValidator<T extends BaseSubmittable> {
    String FAIL_MESSAGE = "Could not find reference target: %s ";
    String FAIL_TEAM_AND_ALIAS_MESSAGE = "Could not find reference for ALIAS: %s in TEAM: %s ";


    public List<SingleValidationResult> validate(
            String idOfSubmittableBeingValidated,
            Collection<AbstractSubsRef> referencesToSubmittables,
            Collection<Submittable> referencedSubmittables) {

        List<SingleValidationResult> results = new ArrayList<>();

        final Map<String, Submittable> sampleAccessionMap = referencedSubmittables.stream()
                .collect(
                        Collectors.toMap(Submittable::getAccession, sample -> sample)
                );
        final Map<String, Submittable> sampleAliasMap = referencedSubmittables.stream()
                .collect(
                        Collectors.toMap(sample -> sample.getAlias() + sample.getTeam().getName(), sample -> sample)
                );


        for (AbstractSubsRef subsRef : referencesToSubmittables) {

            Submittable submittable;

            if (subsRef.getAccession() != null && !subsRef.getAccession().isEmpty()) {
                submittable = sampleAccessionMap.get(subsRef.getAccession());
            } else {
                submittable = sampleAliasMap.get(subsRef.getAlias() + subsRef.getTeam());
            }

            results.add(validate(idOfSubmittableBeingValidated, subsRef, submittable));
        }

        return results;
    }

    public List<SingleValidationResult> validate(
            String idOfSubmittableBeingValidated,
            AbstractSubsRef referenceToSubmittables,
            Collection<Submittable> referencedSubmittables) {

        return this.validate(
                idOfSubmittableBeingValidated,
                Arrays.asList(referenceToSubmittables),
                referencedSubmittables
        );
    }

    public SingleValidationResult validate(String idOfSubmittableBeingValidated, AbstractSubsRef referenceToSubmittable, Submittable referencedSubmittable) {
        SingleValidationResult singleValidationResult = getDefaultSingleValidationResult(idOfSubmittableBeingValidated);

        if (referencedSubmittable == null) {
            if (referenceToSubmittable.getAccession() == null || referenceToSubmittable.getAccession().isEmpty()) {
                singleValidationResult.setMessage(String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, referenceToSubmittable.getAlias(), referenceToSubmittable.getTeam()));
            } else {
                singleValidationResult.setMessage(String.format(FAIL_MESSAGE, referenceToSubmittable.getAccession()));
            }
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);

        } else {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        }

        return singleValidationResult;
    }
}
