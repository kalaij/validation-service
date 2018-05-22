package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;

@Service
public class SchemaService {

    public JsonNode getSchemaFor(Sample sample) throws SchemaNotFoundException {

        // TODO
        throw new SchemaNotFoundException("Could not find schema for Sample");
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
