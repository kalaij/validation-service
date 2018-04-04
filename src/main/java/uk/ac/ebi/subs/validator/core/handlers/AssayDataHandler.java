package uk.ac.ebi.subs.validator.core.handlers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.ReferenceValidator;
import uk.ac.ebi.subs.validator.core.validators.ValidatorHelper;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;

import java.util.Arrays;
import java.util.List;

/**
 * This class responsible for handle {@link AssayData} validation.
 *
 * An AssayData refers to an Assay via {@link uk.ac.ebi.subs.data.component.AssayRef AssayRef} and to
 * a Sample via {@link uk.ac.ebi.subs.data.component.SampleRef SampleRef}.
 */
@Service
@RequiredArgsConstructor
public class AssayDataHandler extends AbstractHandler<AssayDataValidationMessageEnvelope> {

    @NonNull
    private ReferenceValidator refValidator;

    @NonNull
    private AttributeValidator attributeValidator;

    @Override
    List<SingleValidationResult> validateSubmittable(AssayDataValidationMessageEnvelope envelope) {
        AssayData assayData = getAssayDataFromEnvelope(envelope);

        List<SingleValidationResult> results = Arrays.asList(
                refValidator.validate(assayData.getId(), assayData.getAssayRef(), envelope.getAssay()),
                refValidator.validate(assayData.getId(), assayData.getSampleRef(), envelope.getSample())
        );

        return results;
    }

    @Override
    List<SingleValidationResult> validateAttributes(ValidationMessageEnvelope envelope) {
        AssayData assayData = getAssayDataFromEnvelope(envelope);

        return ValidatorHelper.validateAttribute(assayData.getAttributes(), assayData.getId(), attributeValidator);
    }

    private AssayData getAssayDataFromEnvelope(ValidationMessageEnvelope envelope) {
        return (AssayData) envelope.getEntityToValidate();
    }

}
