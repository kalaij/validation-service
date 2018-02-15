package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.validator.core.validators.*;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.List;

/**
 * This class responsible for handle {@link Assay} validation.
 *
 * An assay refers to a study via {@link uk.ac.ebi.subs.data.component.StudyRef StudyRef} and to
 * one or multiple samples via {@link uk.ac.ebi.subs.data.component.SampleUse SampleUse}.
 */
@Service
public class AssayHandler extends AbstractHandler<AssayValidationMessageEnvelope> {

    private ReferenceValidator studyRefValidator;

    private ReferenceValidator sampleRefValidator;

    private AttributeValidator attributeValidator;

    public AssayHandler(ReferenceValidator studyRefValidator, ReferenceValidator sampleRefValidator,
                        AttributeValidator attributeValidator) {
        this.studyRefValidator = studyRefValidator;
        this.sampleRefValidator = sampleRefValidator;
        this.attributeValidator = attributeValidator;
    }

    @Override
    SingleValidationResult validateSubmittable(AssayValidationMessageEnvelope envelope) {
        Assay assay = getAssayFromEnvelope(envelope);

        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Core, assay.getId());
        studyRefValidator.validate(envelope.getStudy(), assay.getStudyRef(), singleValidationResult);
        final Submittable[] submittables = envelope.getSampleList().toArray(new Submittable[envelope.getSampleList().size()]);
        final SampleRef[] sampleRefs = assay.getSampleUses().stream().map(SampleUse::getSampleRef).toArray(SampleRef[]::new);
        sampleRefValidator.validate(submittables, sampleRefs, singleValidationResult);

        return singleValidationResult;
    }

    @Override
    List<SingleValidationResult> validateAttributes(ValidationMessageEnvelope envelope) {
        Assay assay = getAssayFromEnvelope(envelope);

        return ValidatorHelper.validateAttribute(assay.getAttributes(), assay.getId(), attributeValidator);
    }

    private Assay getAssayFromEnvelope(ValidationMessageEnvelope envelope) {
        return (Assay) envelope.getEntityToValidate();
    }
}
