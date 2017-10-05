package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.StudyTypeValidator;
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

    public StudyHandler(StudyTypeValidator studyTypeValidator, AttributeValidator attributeValidator) {
        this.studyTypeValidator = studyTypeValidator;
        this.attributeValidator = attributeValidator;
    }

    @Override
    SingleValidationResult validateSubmittable(ValidationMessageEnvelope envelope) {
        Study study = getStudyFromEnvelope(envelope);

        return studyTypeValidator.validate(study);
    }

    @Override
    List<SingleValidationResult> validateAttributes(ValidationMessageEnvelope envelope) {
        Study study = getStudyFromEnvelope(envelope);
        return attributeValidator.validate(study.getAttributes(), study.getId());
    }

    private Study getStudyFromEnvelope(ValidationMessageEnvelope envelope) {
        return (Study) envelope.getEntityToValidate();
    }

}
