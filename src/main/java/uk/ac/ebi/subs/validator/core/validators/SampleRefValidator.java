package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.List;

@Service
@EnableMongoRepositories(basePackageClasses = SampleRepository.class)
public class SampleRefValidator extends AbstractReferenceValidator {

    String FAIL_TEAM_AND_ALIAS_MESSAGE = "Could not find reference for ALIAS: %s in TEAM: %s";
    String FAIL_MESSAGE = "Could not find reference target: %s .";

    @Autowired
    public SampleRepository sampleRepository;

    /**
     * An AssayData refers to a Sample via a SampleRef
     * @param sampleRef
     * @param singleValidationResult
     */
    @Override
    public void validate(AbstractSubsRef sampleRef, SingleValidationResult singleValidationResult) {
        Sample sample;
        if (sampleRef.getAccession() != null && !sampleRef.getAccession().isEmpty()) {
            sample = sampleRepository.findByAccession(sampleRef.getAccession());
        } else {
            sample = sampleRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc(sampleRef.getTeam(), sampleRef.getAlias());
        }

        if (singleValidationResult.getValidationStatus().equals(ValidationStatus.Pending)) {
            initializeSingleValidationResult(sample, sampleRef, singleValidationResult);
        } else {
            updateSingleValidationResult(sample, sampleRef, singleValidationResult);
        }
    }

    /**
     * A Sample refers to other samples and itself via SampleRelationships
     * @param sampleRelationshipList
     * @param singleValidationResult
     * @return
     */
    public void validateSampleRelationships(List<SampleRelationship> sampleRelationshipList, SingleValidationResult singleValidationResult) {
        StringBuilder referencesTargets = new StringBuilder();

        for (SampleRelationship sampleRelationship : sampleRelationshipList) {
            Sample sample;
            if (sampleRelationship.getAccession() != null && !sampleRelationship.getAccession().isEmpty()) {
                sample = sampleRepository.findByAccession(sampleRelationship.getAccession());
            } else {
                sample = sampleRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc(sampleRelationship.getTeam(), sampleRelationship.getAlias());
            }

            if (sample == null) {
                if (sampleRelationship.getAccession() != null && !sampleRelationship.getAccession().isEmpty()) {

                    if(referencesTargets.toString().isEmpty()) {
                        referencesTargets.append(String.format(FAIL_MESSAGE, sampleRelationship.getAccession()));
                    } else {
                        referencesTargets.append(", " + String.format(FAIL_MESSAGE, sampleRelationship.getAccession()));
                    }

                } else {

                    if(referencesTargets.toString().isEmpty()) {
                        referencesTargets.append(String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, sampleRelationship.getAlias(), sampleRelationship.getTeam()));
                    } else {
                        referencesTargets.append(", " + String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, sampleRelationship.getAlias(), sampleRelationship.getTeam()));
                    }
                }
            }

            if (singleValidationResult.getValidationStatus().equals(ValidationStatus.Pending)) {
                initializeSingleValidationResult(referencesTargets, singleValidationResult);
            } else {
                updateSingleValidationResult(referencesTargets, singleValidationResult);
            }
        }
    }

    /**
     * An Assay refers to multiple Samples via SampleUses
     * @param sampleUseList
     * @param singleValidationResult
     */
    public void validateSampleUses(List<SampleUse> sampleUseList, SingleValidationResult singleValidationResult) {
        StringBuilder referencesTargets = new StringBuilder();

        for (SampleUse sampleUse : sampleUseList) {

            Sample sample;
            if (sampleUse.getSampleRef().getAccession() != null && !sampleUse.getSampleRef().getAccession().isEmpty()) {
                sample = sampleRepository.findByAccession(sampleUse.getSampleRef().getAccession());
            } else {
                sample = sampleRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc(sampleUse.getSampleRef().getTeam(), sampleUse.getSampleRef().getAlias());
            }

            if (sample == null) {
                if (sampleUse.getSampleRef().getAccession() != null && !sampleUse.getSampleRef().getAccession().isEmpty()) {

                    if(referencesTargets.toString().isEmpty()) {
                        referencesTargets.append(String.format(FAIL_MESSAGE, sampleUse.getSampleRef().getAccession()));
                    } else {
                        referencesTargets.append(", " + String.format(FAIL_MESSAGE, sampleUse.getSampleRef().getAccession()));
                    }

                } else {

                    if(referencesTargets.toString().isEmpty()) {
                        referencesTargets.append(String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, sampleUse.getSampleRef().getAlias(), sampleUse.getSampleRef().getTeam()));
                    } else {
                        referencesTargets.append(", " + String.format(FAIL_TEAM_AND_ALIAS_MESSAGE, sampleUse.getSampleRef().getAlias(), sampleUse.getSampleRef().getTeam()));
                    }
                }
            }

            if (singleValidationResult.getValidationStatus().equals(ValidationStatus.Pending)) {
                initializeSingleValidationResult(referencesTargets, singleValidationResult);
            } else {
                updateSingleValidationResult(referencesTargets, singleValidationResult);
            }
        }
    }

}
