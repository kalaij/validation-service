package uk.ac.ebi.subs.validator.coordinator;

import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;


public abstract class ValidationMessageEnvelopeExpander<T extends ValidationMessageEnvelope> {
    abstract void expandEnvelope(T validationMessageEnvelope);

}
