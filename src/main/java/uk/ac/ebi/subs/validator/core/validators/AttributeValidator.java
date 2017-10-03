package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.subs.validator.core.validators.ValidatorHelper.getDefaultSingleValidationResult;

@Service
public class AttributeValidator {

    private static final String FAIL_MESSAGE = "An attribute value can't be null. Attribute - {}";

    public List<SingleValidationResult> validate(List<Attribute> attributes, String id) {
        List<SingleValidationResult> validationResults = new ArrayList<>();

        attributes.forEach(attribute -> {
            if (attribute.getValue() == null) {
                SingleValidationResult singleValidationResult = getDefaultSingleValidationResult(id);
                singleValidationResult.setMessage(String.format(FAIL_MESSAGE, attribute.getName()));
                singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);

                validationResults.add(singleValidationResult);
            }
        });

        return validationResults;
    }

}
