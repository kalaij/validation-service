package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.SampleRefValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.List;

/**
 * This class responsible for handle {@link Sample} validation.
 *
 * A sample may refer to other samples or itself
 * using {@link  uk.ac.ebi.subs.data.component.SampleRelationship SampleRelationship}
 */
@Service
public class SampleHandler extends AbstractHandler {

    @Autowired
    private SampleRefValidator sampleRefValidator;
    @Autowired
    private AttributeValidator attributeValidator;

    @Override
    SingleValidationResult validateSubmittable(ValidationMessageEnvelope envelope) {
        Sample sample = getSampleFromEnvelope(envelope);

        SingleValidationResult singleValidationResult =
                new SingleValidationResult(ValidationAuthor.Core, sample.getId());
        sampleRefValidator.validateSampleRelationships(sample.getSampleRelationships(), singleValidationResult);

        return singleValidationResult;
    }

    @Override
    List<SingleValidationResult> validateAttributes(ValidationMessageEnvelope envelope) {
        Sample sample = getSampleFromEnvelope(envelope);
        return attributeValidator.validate(sample.getAttributes(), sample.getId());
    }

    private Sample getSampleFromEnvelope(ValidationMessageEnvelope envelope) {
        return (Sample) envelope.getEntityToValidate();
    }
}
