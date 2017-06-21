package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.validator.core.validators.AssayRefValidator;
import uk.ac.ebi.subs.validator.core.validators.SampleRefValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;

@Service
public class AssayDataHandler implements Handler {

    @Autowired
    AssayRefValidator assayRefValidator;
    @Autowired
    SampleRefValidator sampleRefValidator;

    /**
     * An AssayData refers to an Assay via {@link uk.ac.ebi.subs.data.component.AssayRef AssayRef} and to
     * a Sample via {@link uk.ac.ebi.subs.data.component.SampleRef SampleRef}
     * @param envelope
     * @return
     */
    @Override
    public SingleValidationResult handleValidationRequest(ValidationMessageEnvelope envelope) {
        AssayData assayData = (AssayData) envelope.getEntityToValidate();

        SingleValidationResult singleValidationResult = generateBlankSingleValidationResult(assayData.getId(), envelope.getValidationResultUUID());
        assayRefValidator.validate(assayData.getAssayRef(), singleValidationResult);

        sampleRefValidator.validate(assayData.getSampleRef(), singleValidationResult);

        return singleValidationResult;
    }
}
