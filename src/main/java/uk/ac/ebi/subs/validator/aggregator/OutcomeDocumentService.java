package uk.ac.ebi.subs.validator.aggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.EntityValidationOutcome;
import uk.ac.ebi.subs.validator.data.ValidationOutcome;
import uk.ac.ebi.subs.validator.repository.ValidationOutcomeRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OutcomeDocumentService {

    @Autowired
    private ValidationOutcomeRepository repository;

    public boolean updateValidationOutcome(EntityValidationOutcome validationOutcome) {
        ValidationOutcome outcome = repository.findOne(validationOutcome.getOutcomeDocumentUUID());

        if(isLatestVersion(outcome.getSubmissionId(), outcome.getEntityUuid(), Double.valueOf(outcome.getVersion()))) {
            outcome.getValidationResults().add(validationOutcome);
            outcome.getExpectedOutcomes().put(validationOutcome.getArchive(), true);
            repository.save(outcome);
            return true;
        }
        return false;
    }

    public boolean isLatestVersion(String submissionId, String entityUuid, double thisOutcomeVersion) {
        List<ValidationOutcome> validationOutcomes = repository.findBySubmissionIdAndEntityUuid(submissionId, entityUuid);

        if (validationOutcomes.size() > 0) {
            List<Double> doubleVersions = validationOutcomes.stream()
                    .map(validationOutcome -> Double.valueOf(validationOutcome.getVersion()))
                    .collect(Collectors.toList());

            double max = Collections.max(doubleVersions);
            if (max > thisOutcomeVersion) {
                return false;
            }
        }
        return true;
    }
}
