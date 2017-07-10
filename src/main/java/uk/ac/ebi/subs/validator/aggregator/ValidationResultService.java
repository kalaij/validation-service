package uk.ac.ebi.subs.validator.aggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

@Service
public class ValidationResultService {

    @Autowired
    private ValidationResultRepository repository;

    public boolean updateValidationResult(SingleValidationResultsEnvelope envelope) {
        ValidationResult validationResult = repository.findOne(envelope.getValidationResultUUID());

        if (validationResult != null) {
            if (validationResult.getVersion() == envelope.getValidationResultVersion()) {
                validationResult.getExpectedResults().put(envelope.getValidationAuthor(), envelope.getSingleValidationResults());
                repository.save(validationResult);
                return true;
            }
        }
        return false;
    }

}