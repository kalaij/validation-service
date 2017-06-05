package uk.ac.ebi.subs.validator.aggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ValidationResultService {

    @Autowired
    private ValidationResultRepository repository;

    public boolean updateValidationResult(SingleValidationResult singleValidationResult) {
        ValidationResult validationResult = repository.findOne(singleValidationResult.getValidationResultUUID());

        if (validationResult != null) {
            if (isLatestVersion(validationResult.getSubmissionId(), validationResult.getEntityUuid(), validationResult.getVersion())) {
                validationResult.getValidationResults().add(singleValidationResult);
                validationResult.getExpectedResults().put(singleValidationResult.getArchive(), true);
                repository.save(validationResult);
                return true;
            }
        }
        return false;
    }

    public boolean isLatestVersion(String submissionId, String entityUuid, int thisValidationResultVersion) {
        List<ValidationResult> validationResults = repository.findBySubmissionIdAndEntityUuid(submissionId, entityUuid);

        if (validationResults.size() > 0) {
            List<Integer> versions = validationResults.stream()
                    .map(validationResult -> validationResult.getVersion())
                    .collect(Collectors.toList());

            int max = Collections.max(versions);
            if (max > thisValidationResultVersion) {
                return false;
            }
        }
        return true;
    }
}
