package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

@Service
public class StudyHandler implements Handler {

    /**
     * A Study refers to no other object.
     * @param envelope
     * @return
     */
    @Override
    public SingleValidationResult handleValidationRequest(ValidationMessageEnvelope envelope) {
        Study study = (Study) envelope.getEntityToValidate();
        SingleValidationResult singleValidationResult = generateBlankSingleValidationResult(study.getId(), envelope.getValidationResultUUID());

        singleValidationResult.setValidationStatus(ValidationStatus.Pass);

        return singleValidationResult;
    }
}
