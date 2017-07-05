package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.validator.util.BlankValidationResultMaps;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

import java.util.UUID;

@Service
public class ValidationResultService {
    private static Logger logger = LoggerFactory.getLogger(ValidationResultService.class);

    @Autowired
    private ValidationResultRepository repository;


    public ValidationResult generateValidationResultDocument(Sample sample, String submissionId){
        ValidationResult validationResult = createOrUpdateValidationResult(sample, submissionId);
        if (validationResult.getVersion() == 1) {
            validationResult.setExpectedResults(BlankValidationResultMaps.forSample());
        }

        repository.save(validationResult);

        return validationResult;
    }

    public ValidationResult generateValidationResultDocument(Study study, String submissionId){
        ValidationResult validationResult = createOrUpdateValidationResult(study, submissionId);
        if (validationResult.getVersion() == 1) {
            validationResult.setExpectedResults(BlankValidationResultMaps.forStudy());
        }

        repository.save(validationResult);

        return validationResult;
    }

    public ValidationResult generateValidationResultDocument(Assay assay, String submissionId){
        ValidationResult validationResult = createOrUpdateValidationResult(assay, submissionId);
        if (validationResult.getVersion() == 1) {
            validationResult.setExpectedResults(BlankValidationResultMaps.forAssay());
        }

        repository.save(validationResult);

        return validationResult;
    }

    public ValidationResult generateValidationResultDocument(AssayData assayData, String submissionId){
        ValidationResult validationResult = createOrUpdateValidationResult(assayData, submissionId);
        if (validationResult.getVersion() == 1) {
            validationResult.setExpectedResults(BlankValidationResultMaps.forAssayData());
        }

        repository.save(validationResult);

        return validationResult;
    }

    private ValidationResult createOrUpdateValidationResult(Submittable submittable, String submissionId) {
        String submittableUuid = submittable.getId();
        ValidationResult validationResult = repository.findByEntityUuid(submittableUuid);
        if (validationResult != null) {
            validationResult.setVersion(validationResult.getVersion() + 1);
        } else {
            validationResult = new ValidationResult();
            validationResult.setUuid(UUID.randomUUID().toString());
            validationResult.setSubmissionId(submissionId);
            validationResult.setEntityUuid(submittableUuid);

            validationResult.setVersion(1);
        }

        return validationResult;
    }
}
