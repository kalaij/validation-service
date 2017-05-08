package uk.ac.ebi.subs.validator.aggregator;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.subs.validator.data.EntityValidationOutcome;
import uk.ac.ebi.subs.validator.data.ValidationOutcome;
import uk.ac.ebi.subs.validator.repository.ValidationOutcomeRepository;

/**
 * This class updates the {@code ValidationOutcome} document in the MongoDB database with the various entities
 * validation outcomes. Also set the flag to {@code true} in the validation result map, when we updated the validation
 * result of that specific archive.
 *
 * Created by karoly on 05/05/2017.
 */
public class ValidationAggregator {

    @Autowired
    private ValidationOutcomeRepository repository;

    public void updateValidationOutcome(EntityValidationOutcome validationOutcome) {
        ValidationOutcome validationOutcomeDocument = repository.findOne(validationOutcome.getUuid());

        validationOutcomeDocument.getValidationResults().add(validationOutcome);
        validationOutcomeDocument.getExpectedOutcomes().put(validationOutcome.getArchive(), true);

        repository.save(validationOutcomeDocument);
    }
}
