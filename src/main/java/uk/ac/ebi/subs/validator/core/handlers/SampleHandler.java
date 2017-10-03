package uk.ac.ebi.subs.validator.core.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.SampleRefValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.ArrayList;
import java.util.List;

@Service
public class SampleHandler extends AbstractHandler {

    @Autowired
    private SampleRefValidator sampleRefValidator;
    @Autowired
    private AttributeValidator attributeValidator;

    /**
     * A sample may refer to other samples or itself using {@link  uk.ac.ebi.subs.data.component.SampleRelationship SampleRelationship}
     * @param envelope
     * @return
     */
    @Override
    public SingleValidationResultsEnvelope handleValidationRequest(ValidationMessageEnvelope envelope) {
        Sample sample = (Sample) envelope.getEntityToValidate();
        List<SingleValidationResult> resultList = new ArrayList<>();

        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Core, sample.getId());
        sampleRefValidator.validateSampleRelationships(sample.getSampleRelationships(), singleValidationResult);
        checkValidationStatus(singleValidationResult);
        resultList.add(singleValidationResult);

        resultList.addAll(attributeValidator.validate(sample.getAttributes(), sample.getId()));

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = generateSingleValidationResultsEnvelope(envelope, resultList);
        return singleValidationResultsEnvelope;
    }
}
