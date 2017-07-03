package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ValidationResultService {
    private static Logger logger = LoggerFactory.getLogger(ValidationResultService.class);

    @Autowired
    private ValidationResultRepository repository;

    public ValidationResult generateValidationResultDocument(BaseSubmittable submittable, String submissionId) {
        logger.debug("Creating Validation Result Document for {} from submission {}",
                submittable.getClass().getSimpleName(), submissionId);

        return createOrUpdateValidationResult(submissionId, submittable.getId());
    }

    ValidationResult createOrUpdateValidationResult(String submissionId, String submittableUuid) {
        ValidationResult validationResult = repository.findByEntityUuid(submittableUuid);
        if (validationResult != null) {
            validationResult.setVersion(validationResult.getVersion() + 1);
        } else {
            validationResult = new ValidationResult();
            validationResult.setUuid(UUID.randomUUID().toString());
            validationResult.setSubmissionId(submissionId);
            validationResult.setEntityUuid(submittableUuid);

            validationResult.setVersion(1);

            Map<ValidationAuthor, Boolean> expectedResults = new HashMap<>();
            for (ValidationAuthor validationAuthor : Arrays.asList(ValidationAuthor.Core, ValidationAuthor.Biosamples,
                    ValidationAuthor.Ena, ValidationAuthor.Taxonomy)) {
                expectedResults.put(validationAuthor, false);
            }
            validationResult.setExpectedResults(expectedResults);
        }

        repository.save(validationResult);

        return validationResult;
    }
}
