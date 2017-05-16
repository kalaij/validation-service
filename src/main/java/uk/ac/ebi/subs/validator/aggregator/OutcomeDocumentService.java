package uk.ac.ebi.subs.validator.aggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.EntityValidationOutcome;
import uk.ac.ebi.subs.validator.data.ValidationOutcome;
import uk.ac.ebi.subs.validator.repository.ValidationOutcomeRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class updates the {@code ValidationOutcome} document in the MongoDB database with the various entities
 * validation outcomes. Also set the flag to {@code true} in the validation result map, when we updated the validation
 * result of that specific archive.
 *
 * Created by karoly on 05/05/2017.
 */
@Service
public class OutcomeDocumentService {

    @Autowired
    private ValidationOutcomeRepository repository;

    public boolean updateValidationOutcome(EntityValidationOutcome validationOutcome) {
        ValidationOutcome outcome = repository.findOne(validationOutcome.getOutcomeDocumentUUID());

        if(checkVersion(outcome.getSubmissionId(), outcome.getEntityUuid(), Double.valueOf(outcome.getVersion()))) {
            outcome.getValidationResults().add(validationOutcome);
            outcome.getExpectedOutcomes().put(validationOutcome.getArchive(), true);
            repository.save(outcome);
            return true;
        }
        return false;
    }

    private boolean checkVersion(String submissionId, String entityUuid, double current) {
        List<ValidationOutcome> validationOutcomes = repository.findBySubmissionIdAndEntityUuid(submissionId, entityUuid);

        if (validationOutcomes.size() > 0) {
            List<String> versions = validationOutcomes.stream()
                    .map(validationOutcome -> validationOutcome.getVersion())
                    .collect(Collectors.toList());

            List<Double> doubleVersions = versions.stream().map(Double::valueOf).collect(Collectors.toList());
            double max = Collections.max(doubleVersions);
            if (max > current) {
                return false;
            }
        }
        return true;
    }
}
