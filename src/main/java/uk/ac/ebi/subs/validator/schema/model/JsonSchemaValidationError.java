package uk.ac.ebi.subs.validator.schema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class JsonSchemaValidationError {

    private List<String> errors;
    private String dataPath;

    public JsonSchemaValidationError() {}

    public JsonSchemaValidationError(List<String> errors, String dataPath) {
        this.errors = errors;
        this.dataPath = dataPath;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getDataPath() {
        return dataPath;
    }

    @JsonIgnore
    public String getErrorsAsString() {
        return String.join(", ", errors) + ".";
    }

    @Override
    public String toString() {
        return "JsonSchemaValidationError{" +
                "errors=" + errors +
                ", dataPath='" + dataPath + '\'' +
                '}';
    }
}
