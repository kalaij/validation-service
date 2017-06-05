package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
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

    public ValidationResult generateValidationResultDocument(BaseSubmittable submittable, String submissionId) {
        logger.debug("Creating Validation Outcome Document for {} from submission {}",
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

    private ValidationResult generateValidationResult(String submissionId, BaseSubmittable submittable, int version) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setUuid(UUID.randomUUID().toString());
        validationResult.setSubmissionId(submissionId);
        validationResult.setEntityUuid(submittable.getId());

        validationResult.setVersion(version);

        Map<Archive, Boolean> expectedResults = new HashMap<>();
        for (Archive archive : Arrays.asList(Archive.BioSamples, Archive.Ena, Archive.ArrayExpress)) {
            expectedResults.put(archive, false);
        }
        validationResult.setExpectedResults(expectedResults);
        return validationResult;
    }

    private void deleteObsoleteValidationResults(List<ValidationResult> validationResults) {
        repository.delete(validationResults);
    }
}
