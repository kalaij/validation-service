package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.ReferenceValidator;
import uk.ac.ebi.subs.validator.core.validators.StudyTypeValidator;
import uk.ac.ebi.subs.validator.core.validators.ValidatorHelper;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;

import java.util.List;

/**
 * This class responsible for handle {@link Study} validation.
 *
 * A Study refers to no other object.
 * A study must have a studyType.
 */
@Service
public class StudyHandler extends AbstractHandler {

    private StudyTypeValidator studyTypeValidator;

    private AttributeValidator attributeValidator;
    // wired in but not used
    private ReferenceValidator projectRefValidator;

    public StudyHandler(StudyTypeValidator studyTypeValidator, AttributeValidator attributeValidator, ReferenceValidator projectRefValidator) {
        this.studyTypeValidator = studyTypeValidator;
        this.attributeValidator = attributeValidator;
        this.projectRefValidator = projectRefValidator;

    }

    @Override
    SingleValidationResult validateSubmittable(ValidationMessageEnvelope envelope) {
        Study study = getStudyFromEnvelope(envelope);

        return studyTypeValidator.validate(study);
    }

    @Override
    List<SingleValidationResult> validateAttributes(ValidationMessageEnvelope envelope) {
        Study study = getStudyFromEnvelope(envelope);

        return ValidatorHelper.validateAttribute(study.getAttributes(), study.getId(), attributeValidator);
    }

    private Study getStudyFromEnvelope(ValidationMessageEnvelope envelope) {
        return (Study) envelope.getEntityToValidate();
    }

}
