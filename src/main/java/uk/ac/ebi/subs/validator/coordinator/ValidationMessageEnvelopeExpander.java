package uk.ac.ebi.subs.validator.coordinator;

import uk.ac.ebi.subs.repository.model.StoredSubmittable;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;


public abstract class ValidationMessageEnvelopeExpander<T extends ValidationMessageEnvelope> {
    abstract void expandEnvelope(T validationMessageEnvelope, String submissionId);

    boolean addToValidationEnvelope(StoredSubmittable storedSubmittable, String submissionId) {
        return storedSubmittable != null && storedSubmittable.getSubmission().getId().equals(submissionId) ? true : false;
    }

}
