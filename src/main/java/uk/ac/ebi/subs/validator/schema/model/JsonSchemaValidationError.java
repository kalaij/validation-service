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
        String errorString = "";
        for (int i = 0; i < errors.size(); i++) {
            if (i == 0) {
                errorString = errors.get(i);
            } else {
                errorString += ", " + errors.get(i);
            }

            if(i == errors.size()-1) {
                errorString += ".";
            }
        }
        return errorString;
    }

    @Override
    public String toString() {
        return "JsonSchemaValidationError{" +
                "errors=" + errors +
                ", dataPath='" + dataPath + '\'' +
                '}';
    }
}
