package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.validator.schema.model.JsonSchemaValidationError;
import uk.ac.ebi.subs.validator.schema.model.JsonSchemaValidationRequestBody;

import java.util.Arrays;
import java.util.List;

@Service
public class JsonSchemaValidationService {
    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaValidationService.class);
    private static final String JSON_SCHEMA_VALIDATOR = "https://usi-json-schema-validator.herokuapp.com/validate";

    private RestTemplate restTemplate;

    public JsonSchemaValidationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<JsonSchemaValidationError> validate(JsonNode schema, JsonNode submittable) {
        logger.trace("Calling json-schema-validator...");
        ResponseEntity<JsonSchemaValidationError[]> response = restTemplate.postForEntity(
                JSON_SCHEMA_VALIDATOR,
                new JsonSchemaValidationRequestBody(schema, submittable),
                JsonSchemaValidationError[].class
        );
        return Arrays.asList(response.getBody());
    }
}
