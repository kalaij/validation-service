package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.List;

@Service
public class SampleRelationshipValidator {

    public SingleValidationResult validate(List<SampleRelationship> sampleRelationshipList, SingleValidationResult singleValidationResult) {

        //TODO

        singleValidationResult.setMessage("");
        singleValidationResult.setValidationStatus(ValidationStatus.Pass);


        return singleValidationResult;
    }

}
