package uk.ac.ebi.subs.validator.schema.model;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonSchemaValidationRequestBody {
    private JsonNode schema;
    private JsonNode object;

    public JsonSchemaValidationRequestBody(JsonNode schema, JsonNode object) {
        this.schema = schema;
        this.object = object;
    }

    public JsonNode getSchema() {
        return schema;
    }

    public void setSchema(JsonNode schema) {
        this.schema = schema;
    }

    public JsonNode getObject() {
        return object;
    }

    public void setObject(JsonNode object) {
        this.object = object;
    }
}
