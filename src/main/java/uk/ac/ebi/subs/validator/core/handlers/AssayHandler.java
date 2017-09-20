package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.validator.core.validators.SampleRefValidator;
import uk.ac.ebi.subs.validator.core.validators.StudyRefValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.Collections;

@Service
public class AssayHandler extends AbstractHandler {

    @Autowired
    StudyRefValidator studyRefValidator;
    @Autowired
    SampleRefValidator sampleRefValidator;

    /**
     * An assay refers to a study via {@link uk.ac.ebi.subs.data.component.StudyRef StudyRef} and to
     * one or multiple samples via {@link uk.ac.ebi.subs.data.component.SampleUse SampleUse}
     * @param envelope
     * @return
     */
    @Override
    public SingleValidationResultsEnvelope handleValidationRequest(ValidationMessageEnvelope envelope) {
        Assay assay = (Assay) envelope.getEntityToValidate();

        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Core, assay.getId());

        studyRefValidator.validate(assay.getStudyRef(), singleValidationResult);
        sampleRefValidator.validateSampleUses(assay.getSampleUses(), singleValidationResult);

        checkValidationStatus(singleValidationResult);

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = generateSingleValidationResultsEnvelope(envelope, Collections.singletonList(singleValidationResult));

        return singleValidationResultsEnvelope;
    }

}
