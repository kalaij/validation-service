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

        if (outcome != null) {
            if (isLatestVersion(outcome.getSubmissionId(), outcome.getEntityUuid(), outcome.getVersion())) {
                outcome.getValidationResults().add(validationOutcome);
                outcome.getExpectedOutcomes().put(validationOutcome.getArchive(), true);
                repository.save(outcome);
                return true;
            }
        }
        return false;
    }

    public boolean isLatestVersion(String submissionId, String entityUuid, int thisOutcomeVersion) {
        List<ValidationOutcome> validationOutcomes = repository.findBySubmissionIdAndEntityUuid(submissionId, entityUuid);

        if (validationOutcomes.size() > 0) {
            List<Integer> versions = validationOutcomes.stream()
                    .map(validationOutcome -> validationOutcome.getVersion())
                    .collect(Collectors.toList());

            int max = Integer.valueOf(Collections.max(versions));
            if (max > Integer.valueOf(thisOutcomeVersion)) {
                return false;
            }
        }
        return true;
    }
}
