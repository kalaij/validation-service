package uk.ac.ebi.subs.validator.coordinator;

import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.repository.model.StoredSubmittable;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;

import java.util.ArrayList;
import java.util.List;

public abstract class ValidationMessageEnvelopeExpander<T extends ValidationMessageEnvelope> {
    abstract void expandEnvelope(T validationMessageEnvelope, String submissionId);

    boolean addToValidationEnvelope(StoredSubmittable storedSubmittable, String submissionId) {
        return storedSubmittable.getId().equals(submissionId) ? true : false;
    }

}
