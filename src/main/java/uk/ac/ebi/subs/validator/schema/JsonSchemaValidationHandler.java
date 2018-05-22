package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.schema.model.JsonSchemaValidationError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.subs.validator.util.ValidationHelper.generatePassingSingleValidationResult;
import static uk.ac.ebi.subs.validator.util.ValidationHelper.generateSingleValidationResultsEnvelope;

@Service
public class JsonSchemaValidationHandler {

    private JsonSchemaValidationService validationService;
    private ObjectMapper mapper = new ObjectMapper();

    public JsonSchemaValidationHandler(JsonSchemaValidationService validationService) {
        this.validationService = validationService;
    }

    public SingleValidationResultsEnvelope handleSampleValidation(SampleValidationMessageEnvelope envelope)  {

        // TODO - handle logic on which schema to use for validation

        List<JsonSchemaValidationError> jsonSchemaValidationErrors;
        try {
            jsonSchemaValidationErrors = validationService.validate(mapper.readTree("{}"), mapper.valueToTree(envelope.getEntityToValidate()));
        } catch (IOException e) {
            throw new JsonSchemaValidatorException(e.getMessage(), e);
        }

        List<SingleValidationResult> singleValidationResultList = covertToSingleValidationResultList(jsonSchemaValidationErrors, envelope.getEntityToValidate().getId());

        if(singleValidationResultList.isEmpty()) {
             generatePassingSingleValidationResult(envelope.getEntityToValidate().getId(), ValidationAuthor.JsonSchema);
        }

        return generateSingleValidationResultsEnvelope(envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(), singleValidationResultList, ValidationAuthor.JsonSchema);
    }

    private List<SingleValidationResult> covertToSingleValidationResultList(List<JsonSchemaValidationError> errorList, String entityUuid) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        for (JsonSchemaValidationError error : errorList) {
            validationResults.add(generateSchemaSingleValidationResult(error, entityUuid));
        }
        return validationResults;
    }

    private SingleValidationResult generateSchemaSingleValidationResult(JsonSchemaValidationError error, String entityUuid) {
        SingleValidationResult validationResult = new SingleValidationResult();
        validationResult.setValidationAuthor(ValidationAuthor.JsonSchema);
        validationResult.setValidationStatus(SingleValidationResultStatus.Error);
        validationResult.setEntityUuid(entityUuid);
        validationResult.setMessage(error.getDataPath() + " error(s): " + error.getErrorsAsString());
        return validationResult;
    }

}
