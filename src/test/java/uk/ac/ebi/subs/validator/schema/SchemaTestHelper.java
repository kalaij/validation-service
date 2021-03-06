package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import uk.ac.ebi.subs.validator.schema.custom.LocalDateCustomSerializer;

import java.time.LocalDate;

public class SchemaTestHelper {

    public static ObjectMapper createCustomObjectMapper () {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY); // Null fields and empty collections are not included in the serialization.
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new LocalDateCustomSerializer());
        mapper.registerModule(module);
        return mapper;
    }

}
