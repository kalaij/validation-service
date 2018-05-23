package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.schema.custom.CustomHttpMessageConverter;
import uk.ac.ebi.subs.validator.schema.custom.SchemaNotFoundException;

import java.util.Arrays;
import java.util.List;

@Service
public class SchemaService {

    @Value("${sample.schema.url}")
    private String sampleSchemaUrl;

    private RestTemplate restTemplate;

    public SchemaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new CustomHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
    }

    public ObjectNode getSchemaFor(Sample sample) throws SchemaNotFoundException {
        ObjectNode sampleSchema;
        try {
            sampleSchema = restTemplate.getForObject(sampleSchemaUrl, ObjectNode.class);
        } catch (RestClientException e) {
            throw new SchemaNotFoundException(e.getMessage(), e);
        }
        return sampleSchema;
    }

    public JsonNode getSchemaFor(Study study) throws SchemaNotFoundException {

        // TODO
        throw new SchemaNotFoundException("Could not find schema for Study");
    }

    public JsonNode getSchemaFor(Assay assay) throws SchemaNotFoundException {

        // TODO
        throw new SchemaNotFoundException("Could not find schema for Assay");
    }

    public JsonNode getSchemaFor(AssayData assayData) throws SchemaNotFoundException {

        // TODO
        throw new SchemaNotFoundException("Could not find schema for AssayData");
    }
}
