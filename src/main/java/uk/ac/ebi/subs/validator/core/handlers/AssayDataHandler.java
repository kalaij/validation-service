package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.validator.core.validators.AssayRefValidator;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.SampleRefValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.List;

/**
 * This class responsible for handle {@link AssayData} validation.
 *
 * An AssayData refers to an Assay via {@link uk.ac.ebi.subs.data.component.AssayRef AssayRef} and to
 * a Sample via {@link uk.ac.ebi.subs.data.component.SampleRef SampleRef}.
 */
@Service
public class AssayDataHandler extends AbstractHandler {

    private AssayRefValidator assayRefValidator;

    private SampleRefValidator sampleRefValidator;

    private AttributeValidator attributeValidator;

    public AssayDataHandler(AssayRefValidator assayRefValidator, SampleRefValidator sampleRefValidator,
                            AttributeValidator attributeValidator) {
        this.assayRefValidator = assayRefValidator;
        this.sampleRefValidator = sampleRefValidator;
        this.attributeValidator = attributeValidator;
    }

    @Override
    SingleValidationResult validateSubmittable(ValidationMessageEnvelope envelope) {
        AssayData assayData = getAssayDataFromEnvelope(envelope);

        SingleValidationResult singleValidationResult =
                new SingleValidationResult(ValidationAuthor.Core, assayData.getId());
        assayRefValidator.validate(assayData.getAssayRef(), singleValidationResult);
        sampleRefValidator.validate(assayData.getSampleRef(), singleValidationResult);

        return singleValidationResult;
    }

    @Override
    List<SingleValidationResult> validateAttributes(ValidationMessageEnvelope envelope) {
        AssayData assayData = getAssayDataFromEnvelope(envelope);
        return attributeValidator.validate(assayData.getAttributes(), assayData.getId());
    }

    private AssayData getAssayDataFromEnvelope(ValidationMessageEnvelope envelope) {
        return (AssayData) envelope.getEntityToValidate();
    }

}
