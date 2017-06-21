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
public class SampleRefValidator implements ReferenceValidator {

    @Autowired
    public SampleRepository sampleRepository;

    /**
     * An AssayData refers to a Sample via a SampleRef
     * @param sampleRef
     * @param singleValidationResult
     */
    @Override
    public void validate(AbstractSubsRef sampleRef, SingleValidationResult singleValidationResult) {

        Sample sample = sampleRepository.findByAccession(sampleRef.getAccession());

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
        StringBuilder accessions = new StringBuilder();

        for (SampleRelationship sampleRelationship : sampleRelationshipList) {
            Sample sample = sampleRepository.findByAccession(sampleRelationship.getAccession());

            if (sample == null) {
                if(accessions.toString().isEmpty()) {
                    accessions.append(sampleRelationship.getAccession());
                } else {
                    accessions.append(", " + sampleRelationship.getAccession());
                }
            }

            if (singleValidationResult.getValidationStatus().equals(ValidationStatus.Pending)) {
                initializeSingleValidationResult(accessions, singleValidationResult);
            } else {
                updateSingleValidationResult(accessions, singleValidationResult);
            }
        }
    }

    /**
     * An Assay refers to multiple Samples via SampleUses
     * @param sampleUseList
     * @param singleValidationResult
     */
    public void validateSampleUses(List<SampleUse> sampleUseList, SingleValidationResult singleValidationResult) {
        StringBuilder accessions = new StringBuilder();

        for (SampleUse sampleUse : sampleUseList) {
            Sample sample = sampleRepository.findByAccession(sampleUse.getSampleRef().getAccession());

            if (sample == null) {
                if(accessions.toString().isEmpty()) {
                    accessions.append(sampleUse.getSampleRef().getAccession());
                } else {
                    accessions.append(", " + sampleUse.getSampleRef().getAccession());
                }
            }

            if (singleValidationResult.getValidationStatus().equals(ValidationStatus.Pending)) {
                initializeSingleValidationResult(accessions, singleValidationResult);
            } else {
                updateSingleValidationResult(accessions, singleValidationResult);
            }
        }
    }

}
