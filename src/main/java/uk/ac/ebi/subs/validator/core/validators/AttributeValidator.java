package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static uk.ac.ebi.subs.validator.core.validators.ValidatorHelper.getDefaultSingleValidationResult;

@Service
public class AttributeValidator {

    private static final String NULL_NAME_MESSAGE = "An attribute name can't be null.";
    private static final String NULL_VALUE_MESSAGE = "An attribute value can't be null. Attribute - {}";

    public List<SingleValidationResult> validate(String attributeName, Collection<Attribute> attributes, String id) {
        List<SingleValidationResult> validationResults = new ArrayList<>();

        if (attributeName == null) {
            validationResults.add(createSingleErrorResult(id, NULL_NAME_MESSAGE));
        }

        if (attributes != null) {
            attributes.forEach(attribute -> {
                if (attribute.getValue() == null) {
                    validationResults.add(createSingleErrorResult(id, NULL_VALUE_MESSAGE));
                }
            });
        }
        return validationResults;
    }

    private SingleValidationResult createSingleErrorResult(String id, String message) {
        SingleValidationResult singleValidationResult = getDefaultSingleValidationResult(id);
        singleValidationResult.setMessage(message);
        singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);

        return singleValidationResult;
    }

}
