package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.validator.core.validators.AssayRefValidator;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.SampleRefValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssayDataHandler extends AbstractHandler {

    @Autowired
    private AssayRefValidator assayRefValidator;
    @Autowired
    private SampleRefValidator sampleRefValidator;
    @Autowired
    private AttributeValidator attributeValidator;

    /**
     * An AssayData refers to an Assay via {@link uk.ac.ebi.subs.data.component.AssayRef AssayRef} and to
     * a Sample via {@link uk.ac.ebi.subs.data.component.SampleRef SampleRef}
     * @param envelope
     * @return
     */
    @Override
    public SingleValidationResultsEnvelope handleValidationRequest(ValidationMessageEnvelope envelope) {
        AssayData assayData = (AssayData) envelope.getEntityToValidate();
        List<SingleValidationResult> resultList = new ArrayList<>();

        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Core, assayData.getId());
        assayRefValidator.validate(assayData.getAssayRef(), singleValidationResult);
        sampleRefValidator.validate(assayData.getSampleRef(), singleValidationResult);
        checkValidationStatus(singleValidationResult);
        resultList.add(singleValidationResult);

        resultList.addAll(attributeValidator.validate(assayData.getAttributes(), assayData.getId()));

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = generateSingleValidationResultsEnvelope(envelope, resultList);
        return singleValidationResultsEnvelope;
    }
}
