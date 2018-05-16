package uk.ac.ebi.subs.validator.schema.model;

import java.util.List;

public class JsonSchemaValidationError {

    private List<String> errors;
    private String dataPath;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    @Override
    public String toString() {
        return "JsonSchemaValidationError{" +
                "errors=" + errors +
                ", dataPath='" + dataPath + '\'' +
                '}';
    }
}
