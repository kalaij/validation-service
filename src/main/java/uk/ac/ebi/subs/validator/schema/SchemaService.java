package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class SchemaService {

    private RestTemplate restTemplate;

    public SchemaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
    }

    public JsonNode getSchemaFor(String submittableType, String schemaUrl) {
        JsonNode schema;
        try {
            schema = restTemplate.getForObject(schemaUrl, ObjectNode.class);
        } catch (RestClientException e) {
            throw new RestClientException(submittableType + " schema - " + e.getMessage(), e);
        }
        return schema;
    }

}
