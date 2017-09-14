package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.core.validators.StudyTypeValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.Collections;

@Service
public class StudyHandler extends AbstractHandler {

    @Autowired
    StudyTypeValidator studyTypeValidator;

    /**
     * A Study refers to no other object.
     * A study must have a studyType
     * @param envelope
     * @return
     */
    @Override
    public SingleValidationResultsEnvelope handleValidationRequest(ValidationMessageEnvelope envelope) {
        Study study = (Study) envelope.getEntityToValidate();
        SingleValidationResult singleValidationResult = generateBlankSingleValidationResult(study.getId(), envelope.getValidationResultUUID());

        studyTypeValidator.validate(study,singleValidationResult);

        checkValidationStatus(singleValidationResult);

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = generateSingleValidationResultsEnvelope(envelope, Collections.singletonList(singleValidationResult));

        return singleValidationResultsEnvelope;
    }
}
