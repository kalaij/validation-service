package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.ReferenceValidator;
import uk.ac.ebi.subs.validator.core.validators.ValidatorHelper;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.Collection;
import java.util.List;

/**
 * This class responsible for handle {@link Sample} validation.
 *
 * A sample may refer to other samples or itself
 * using {@link  uk.ac.ebi.subs.data.component.SampleRelationship SampleRelationship}
 */
@Service
public class SampleHandler extends AbstractHandler<SampleValidationMessageEnvelope> {

    private ReferenceValidator referenceValidator;

    private AttributeValidator attributeValidator;

    public SampleHandler(ReferenceValidator referenceValidator, AttributeValidator attributeValidator) {
        this.referenceValidator = referenceValidator;
        this.attributeValidator = attributeValidator;
    }

    @Override
    List<SingleValidationResult> validateSubmittable(SampleValidationMessageEnvelope envelope) {
        Sample sample = getSampleFromEnvelope(envelope);

        List<SingleValidationResult> results = referenceValidator.validate(sample.getId(), sample.getSampleRelationships(), envelope.getSampleList());

        return results;
    }

    @Override
    List<SingleValidationResult> validateAttributes(ValidationMessageEnvelope envelope) {
        Sample sample = getSampleFromEnvelope(envelope);

        return ValidatorHelper.validateAttribute(sample.getAttributes(), sample.getId(), attributeValidator);
    }

    private Sample getSampleFromEnvelope(ValidationMessageEnvelope envelope) {
        return (Sample) envelope.getEntityToValidate();
    }
}
