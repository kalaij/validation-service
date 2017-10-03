package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.StudyTypeValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyHandler extends AbstractHandler {

    @Autowired
    private StudyTypeValidator studyTypeValidator;
    @Autowired
    private AttributeValidator attributeValidator;

    /**
     * A Study refers to no other object.
     * A study must have a studyType
     * @param envelope
     * @return
     */
    @Override
    public SingleValidationResultsEnvelope handleValidationRequest(ValidationMessageEnvelope envelope) {
        Study study = (Study) envelope.getEntityToValidate();
        List<SingleValidationResult> resultList = new ArrayList<>();

        resultList.add(studyTypeValidator.validate(study));

        resultList.addAll(attributeValidator.validate(study.getAttributes(), study.getId()));

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = generateSingleValidationResultsEnvelope(envelope, resultList);
        return singleValidationResultsEnvelope;
    }

}
