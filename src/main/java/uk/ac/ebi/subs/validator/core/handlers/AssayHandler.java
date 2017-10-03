package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.SampleRefValidator;
import uk.ac.ebi.subs.validator.core.validators.StudyRefValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AssayHandler extends AbstractHandler {

    @Autowired
    private StudyRefValidator studyRefValidator;
    @Autowired
    private SampleRefValidator sampleRefValidator;
    @Autowired
    private AttributeValidator attributeValidator;

    /**
     * An assay refers to a study via {@link uk.ac.ebi.subs.data.component.StudyRef StudyRef} and to
     * one or multiple samples via {@link uk.ac.ebi.subs.data.component.SampleUse SampleUse}
     * @param envelope
     * @return
     */
    @Override
    public SingleValidationResultsEnvelope handleValidationRequest(ValidationMessageEnvelope envelope) {
        Assay assay = (Assay) envelope.getEntityToValidate();
        List<SingleValidationResult> resultList = new ArrayList<>();

        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Core, assay.getId());
        studyRefValidator.validate(assay.getStudyRef(), singleValidationResult);
        sampleRefValidator.validateSampleUses(assay.getSampleUses(), singleValidationResult);
        checkValidationStatus(singleValidationResult);
        resultList.add(singleValidationResult);

        resultList.addAll(attributeValidator.validate(assay.getAttributes(), assay.getId()));

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = generateSingleValidationResultsEnvelope(envelope, Collections.singletonList(singleValidationResult));
        return singleValidationResultsEnvelope;
    }

}
