package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.schema.custom.CustomHttpMessageConverter;
import uk.ac.ebi.subs.validator.schema.custom.SchemaNotFoundException;

import java.util.List;

@Service
public class SchemaService {

    private RestTemplate restTemplate;

    public SchemaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new CustomHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
    }

    public JsonNode getSchemaFor(String submittableType, String schemaUrl) throws SchemaNotFoundException {
        JsonNode schema;
        try {
            schema = restTemplate.getForObject(schemaUrl, ObjectNode.class);
        } catch (RestClientException e) {
            throw new SchemaNotFoundException(submittableType + " schema - " + e.getMessage(), e);
        }
        return schema;
    }

}
