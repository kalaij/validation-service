package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.dto.AssayDto;
import uk.ac.ebi.subs.data.dto.BaseSubmittableDto;
import uk.ac.ebi.subs.data.dto.SampleDto;
import uk.ac.ebi.subs.data.dto.StudyDto;
import uk.ac.ebi.subs.validator.data.BlankValidationResultMaps;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ValidationResultService {
    private static Logger logger = LoggerFactory.getLogger(ValidationResultService.class);

    @Autowired
    private ValidationResultRepository repository;


    public ValidationResult generateValidationResultDocument(SampleDto sample, String submissionId){
        ValidationResult validationResult = generateVersionedValidationResult(sample, submissionId);
        validationResult.setExpectedResults(BlankValidationResultMaps.forSample());

        return validationResult;
    }

    public ValidationResult generateValidationResultDocument(StudyDto study, String submissionId){
        ValidationResult validationResult = generateVersionedValidationResult(study, submissionId);
        validationResult.setExpectedResults(BlankValidationResultMaps.forStudy());

        return validationResult;
    }

    public ValidationResult generateValidationResultDocument(AssayDto assay, String submissionId){
        ValidationResult validationResult = generateVersionedValidationResult(assay, submissionId);
        validationResult.setExpectedResults(BlankValidationResultMaps.forAssay());

        return validationResult;
    }


    private ValidationResult generateVersionedValidationResult(BaseSubmittableDto submittable, String submissionId) {
        logger.debug("Creating Validation Result Document for {} from submission {}",
                submittable.getClass().getSimpleName(), submissionId);

        int version = getVersion(submissionId, submittable.getId());
        ValidationResult validationResult = generateValidationResult(submissionId, submittable, version);

        return validationResult;
    }

    /**
     * ValidationResult versioning starts on 1 with increments of 1.
     * @param submissionId
     * @param entityUuid
     * @return String version
     */
    public int getVersion(String submissionId, String entityUuid) {
        List<ValidationResult> validationResults = repository.findBySubmissionIdAndEntityUuid(submissionId, entityUuid);

        if (validationResults.size() > 0) {
            List<Integer> versions = validationResults.stream()
                    .map(validationResult -> Integer.valueOf(validationResult.getVersion()))
                    .collect(Collectors.toList());

            int max = Collections.max(versions);
            int version = max + 1;

            deleteObsoleteValidationResults(validationResults);

            return version;
        }
        return 1;
    }

    private ValidationResult generateValidationResult(String submissionId, BaseSubmittableDto submittable, int version) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setUuid(UUID.randomUUID().toString());
        validationResult.setSubmissionId(submissionId);
        validationResult.setEntityUuid(submittable.getId());

        validationResult.setVersion(version);

        return validationResult;
    }

    private void deleteObsoleteValidationResults(List<ValidationResult> validationResults) {
        repository.delete(validationResults);
    }
}
